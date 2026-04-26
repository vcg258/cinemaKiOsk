package com.example.cinemakiosk.service.Ai;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public RagService(ChatClient.Builder builder, VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .build();
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
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
