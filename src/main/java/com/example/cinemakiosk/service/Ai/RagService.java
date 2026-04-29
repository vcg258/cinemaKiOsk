package com.example.cinemakiosk.service.Ai;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class RagService {
    private final ChatMemory chatMemory; // 대화 내용 기억 (DB 저장)
    private final ChatClient chatClient; // LLM 옵션 적용
    private final VectorStore vectorStore; // 벡터 등록
    private final ChatModel chatModel; // 쿼리 압축기 때 사용 (압축변환기의 로그를 보기 위함)

    public RagService(ChatClient.Builder builder,
                      VectorStore vectorStore,
                      ChatMemory chatMemory,
                      ChatModel chatModel) {
        this.chatClient = builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .defaultSystem(
                        """
                        당신은 영화관 직원 교육을 돕는 AI 어시스턴트입니다.
                        제공된 매뉴얼 문서(DB)를 기반으로 정확하게 답변하세요.
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

        // 질문의 최고 임계점수 가져옴
        double maxScore = getMaxSimilarityScore(question, title);
        log.info("질문에 대한 최고 임계점수 : {}", maxScore);

        // 토큰 절약을 위해 LLM에게 보내지 않고 미리 끊음 지정 임계점수 이하면 어차피 관련없는 메뉴얼일 가능성이 높음
        if (maxScore < score) {
            log.info("임계 점수 미달, 검색된 최고 임계 점수 : {}", maxScore);
            return "해당 내용은 매뉴얼에서 찾을 수 없습니다";
        }

        // 검색된 임계점수 기준 (암축 쿼리 변환기 <-> 질문 확장기)
        BaseAdvisor advisor = maxScore > 0.35 ? retrievalAugmentationAdvisorWithCompressor(score, title) :
                retrievalAugmentationAdvisorWithExpander(score, title);

        // chatClient 적용
        String content = chatClient.prompt()
                .user(question)
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(conversationId)
                                .build(),
                        advisor)
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
        ChatClient.Builder builder = ChatClient.builder(chatModel)
                // LLM의 로그를 상세히 알기위해 사용
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
                );

        return CompressionQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
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
     * 검색된 메뉴얼의 최고 임계 점수가 0.35 이상일 경우 사용 (쿼리 압축기)
     * @param score 임계점수
     * @param title 제목
     * @return 검색된 메뉴얼
     */
    private RetrievalAugmentationAdvisor retrievalAugmentationAdvisorWithCompressor(double score, String title) {
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(compressionQueryTransformer()) // 쿼리 압축기
                .documentRetriever(createVectorStoreDocumentRetriever(score, title)) // 저장소에 검색
                .queryAugmenter(contextualQueryAugmenter()) // 공용 프롬프트
                .build();
    }

    /**
     * 검색된 메뉴얼의 최고 임계 점수가 0.35 이하일 경우 사용 (질문 확장기)
     * @param score 임계점수
     * @param title 제목
     * @return 검색된 메뉴얼
     */
    private RetrievalAugmentationAdvisor retrievalAugmentationAdvisorWithExpander(double score, String title) {
        return RetrievalAugmentationAdvisor.builder()
                .queryExpander(multiQueryExpander()) // 질문 확장기
                .documentRetriever(createVectorStoreDocumentRetriever(score, title)) // 저장소에 검색
                .queryAugmenter(contextualQueryAugmenter()) // 공용 프롬프트
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
     * 사용자의 질문에 의해 검색된 context문서와 사용자 질문을 합친 LLM에게 보낼 프롬프트 적용 메서드
     * @return LLM에게 보낼 프롬프트
     */
    private ContextualQueryAugmenter contextualQueryAugmenter() {
        return ContextualQueryAugmenter.builder()
                .promptTemplate(PromptTemplate.builder()
                    .template(
                            """
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
                .build();
    }

    /**
     * 검색을 하기전 유사도 점수 확인을 위한 헬퍼 메서드 (필터링 기능)
     * @param question 질문
     * @param title 제목
     * @return 최고 임계점수
     */
    private double getMaxSimilarityScore(String question, String title) {
        // 질문 검색
        SearchRequest.Builder search = SearchRequest.builder()
                .query(question)
                .topK(3);

        // title이 없거나 비어있지 않을경우 검색할때 title을 사용하여 검색
        if (title != null && !title.isEmpty()) {
            Filter.Expression expression = new FilterExpressionBuilder()
                    .eq("title", title)
                    .build();
            search.filterExpression(expression);
        }

        List<Document> result = vectorStore.similaritySearch(search.build());

        double maxScore = 0.0;
        for (Document doc : result) {
            Double score = doc.getScore();

            // 검색한 임계점수가 null이다 건너뛰기
            if (score == null) {
                continue;
            }
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
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
