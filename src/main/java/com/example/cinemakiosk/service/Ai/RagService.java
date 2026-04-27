package com.example.cinemakiosk.service.Ai;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RagService {
    private final ChatMemory chatMemory; // 대화 내용 기억 (DB 저장)
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final ChatModel chatModel;

    public RagService(ChatClient.Builder builder,
                      VectorStore vectorStore,
                      @Qualifier("pgJdbcTemplate") JdbcTemplate jdbcTemplate,
                      ChatMemory chatMemory,
                      ChatModel chatModel) {
        this.chatClient = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .defaultSystem(
                        """
                        당신은 영화관 직원 교육을 돕는 AI 어시스턴트입니다.
                        제공된 매뉴얼 문서를 기반으로 정확하게 답변하세요.
                        매뉴얼에 없는 내용은 "해당 내용은 매뉴얼에서 찾을 수 없습니다"라고 안내하세요.
                        답변은 한국어로 해주세요.
                        """
                )
                .build();
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.chatModel = chatModel;
    }

    /**
     * LLM 대화
     * @param question 질문
     * @param score 임계점수
     * @param source 출처
     * @param conversationId 사용자 아이디
     * @return LLM 답변
     */
    public String chat(String question, double score, String source, String conversationId) {
        log.info("지정 임계점수 : {}", score);
        log.info("지정된 소스 : {}", source);
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor
                = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(compressionQueryTransformer()) // 압축 쿼리 변환기 추가
                .documentRetriever(createVectorStoreDocumentRetriever(score, source)) // 지정 임계점수에 맞게 검색하는 모듈 추가
                .build();

        String content = chatClient.prompt()
                .user(question)
                .advisors(retrievalAugmentationAdvisor)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
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
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
                );
        CompressionQueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();

        return queryTransformer;
    }

    /**
     * 벡터 저장소에서 유사도 검색
     * @param similarityThreshold 임계점수
     * @param source 출처
     * @return 지정한 유사도에 알맞은 데이터
     */
    private VectorStoreDocumentRetriever createVectorStoreDocumentRetriever(
            double similarityThreshold,
            String source
    ) {
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(similarityThreshold)
                .topK(3) // TODO 상위 3개 잡긴했는데 필요하면 수정
                .filterExpression(() -> {
                    FilterExpressionBuilder builder = new FilterExpressionBuilder();
                    if (source != null && !source.isEmpty()) {
                        return builder.eq("source", source).build();
                    } else {
                        return null;
                    }
                })
                .build();

        return vectorStoreDocumentRetriever;
    }
}
