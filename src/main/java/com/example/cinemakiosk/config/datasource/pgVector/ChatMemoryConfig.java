package com.example.cinemakiosk.config.datasource.pgVector;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class ChatMemoryConfig {

    // PostgresSQL 사용 지정
    @Bean
    public ChatMemoryRepository chatMemoryRepository(@Qualifier("pgJdbcTemplate") JdbcTemplate pgJdbcTemplate) {
        new ResourceDatabasePopulator(
                // 현재는 이중 데이터베이스로 연결할 경우 ChatMemory테이블을 자동으로 생성할 방법은 수동으로 내부에 존재하는 sql문을 가져오는 방법뿐
                new ClassPathResource(
                        "org/springframework/ai/chat/memory/repository/jdbc/schema-postgresql.sql"
                )
        ).execute(pgJdbcTemplate.getDataSource()); // 테이블 생성

        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(pgJdbcTemplate)
                .dialect(JdbcChatMemoryRepositoryDialect.from(pgJdbcTemplate.getDataSource()))
                .build();
    }

    // ChatMemory 상세 설정
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

}
