package com.example.cinemakiosk.config.DataSourceConfig;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PGVectorConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    public DataSourceProperties pgVectorProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "pgVector")
    public DataSource pgVectorDataSource() {
        return pgVectorProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "pgJdbcTemplate")
    public JdbcTemplate pgJdbcTemplate(@Qualifier("pgVector") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "pgVectorTxManager")
    public PlatformTransactionManager pgVectorTxManager(@Qualifier("pgVector") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public VectorStore vectorStore(@Qualifier("pgJdbcTemplate") JdbcTemplate pgJdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(pgJdbcTemplate, embeddingModel)
                .dimensions(1536)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .initializeSchema(true) // VectorDB 자동 생성
                .schemaName("rag") // 스키마 지정
                .build();
    }
}
