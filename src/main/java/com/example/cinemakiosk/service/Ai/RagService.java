package com.example.cinemakiosk.service.Ai;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Log4j2
@Service
public class RagService {
    private final ChatMemory chatMemory; // 대화 내용 기억 (DB 저장)
    private final ChatClient chatClient; // LLM 옵션 적용
    private final VectorStore vectorStore; // 벡터 등록
    private final ChatModel chatModel; // 쿼리 압축기 때 사용 (압축변환기의 로그를 보기 위함)

    public RagService(ChatClient.Builder builder,
                      VectorStore vectorStore,
                      @Qualifier("pgJdbcTemplate") JdbcTemplate jdbcTemplate,
                      ChatMemory chatMemory,
                      ChatModel chatModel) {
        this.chatClient = builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .defaultSystem(
                        """
                        당신은 영화관 직원 교육을 돕는 AI 어시스턴트입니다.
                        제공된 매뉴얼 문서(DB)를 기반으로 정확하게 답변하세요.
                        매뉴얼에 없는 내용은 "해당 내용은 매뉴얼에서 찾을 수 없습니다"라고 안내하세요.
                        답변은 한국어로 해주세요.
                        """
                )
                .build();
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    /**
     * LLM 대화
     * @param question 질문
     * @param score 임계점수
     * @param title 제목
     * @param conversationId 사용자 아이디
     * @return LLM 답변
     */
    public String chat(String question, double score, String title, String conversationId) {
        log.info("지정 임계점수 : {}", score);
        log.info("지정된 제목 : {}", title);
        
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor
                = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(compressionQueryTransformer()) // 압축 쿼리 변환기 추가
                .documentRetriever(createVectorStoreDocumentRetriever(score, title)) // 지정 임계점수에 맞게 검색하는 모듈 추가
                .queryAugmenter(
                        ContextualQueryAugmenter.builder()
                                .promptTemplate(PromptTemplate.builder()
                                        .template("""
                                        아래는 참고할 문서 내용입니다.

                                        --------------
                                        {context}
                                        --------------
                                        
                                        위 내용을 바탕으로 다음 질문에 한국어로 답변하세요.
                                        문서에 관련 내용이 있으면 반드시 그 내용을 기반으로 답변하세요.
                                        
                                        질문: {query}
                                        
                                        답변:
                                        """)
                                        .build())
                                .build()
                )
                .build();

        String content = chatClient.prompt()
                .user(question)
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(conversationId)
                                .build(),
                        retrievalAugmentationAdvisor)
                .call()
                .content();

        return content;
    }

    // Helper Method
    /**
     * 압축 쿼리 변환기
     * @return 압축한 질문쿼리
     */
    private CompressionQueryTransformer compressionQueryTransformer() {
        // LLM의 로그를 상세히 알기위해 사용
        ChatClient.Builder builder = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
                );
        CompressionQueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();

        return queryTransformer;
    }

    /**
     * 질문 확장기 (유사질문 LLM이 추가) 사용자의 프롬프트로 검색을 했을시 임계점수가 너무 낮게 나올 경우 사용
     * @return 사용자의 질문에서 여러개의 유사 질문
     */
    private MultiQueryExpander multiQueryExpander() {
        ChatClient.Builder builder = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
                );

        return MultiQueryExpander.builder()
                .chatClientBuilder(builder)
                .numberOfQueries(2) // TODO 유사 질문 2개로 지정 필요시 수정
                .includeOriginal(true) // 사용자 질문도 포함(날리는 질문수 총 3개)
                .build();
    }

    /**
     * 벡터 저장소에서 유사도 검색
     * @param similarityThreshold 임계점수
     * @param title 제목
     * @return 지정한 유사도에 알맞은 데이터
     */
    private VectorStoreDocumentRetriever createVectorStoreDocumentRetriever(
            double similarityThreshold,
            String title
    ) {

        return VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(similarityThreshold)
                .topK(3) // TODO 상위 3개 잡긴했는데 필요하면 수정
                .filterExpression(() -> {
                    FilterExpressionBuilder builder = new FilterExpressionBuilder();
                    if (title != null && !title.isEmpty()) {
                        return builder.eq("title", title).build();
                    } else {
                        return null;
                    }
                })
                .build();
    }

    /**
     * 디버깅용 질문과 제목 키워드를 입력시 임계점수 확인을 위함
     * @param question 질문
     * @param title 제목
     */
    public void debugScore(String question, String title) {
        Filter.Expression filterExpression = new FilterExpressionBuilder().eq("title", title).build();

        // 임계치 없이 검색 실행
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(3)
                .filterExpression(filterExpression).build();

        List<Document> result = vectorStore.similaritySearch(request);

        for (Document doc : result) {
            log.info("해당하는 DB: {}", doc.getText());
            log.info("실제 유사도 점수: {}", doc.getScore());
        }
    }
}
