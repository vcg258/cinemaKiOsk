package com.example.cinemakiosk.batch;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class MemberCleanupWriter implements ItemWriter<String> {

    private final JdbcTemplate jdbcTemplate;

    public MemberCleanupWriter(@Qualifier("mariaDBJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void write(Chunk<? extends String> phones) {
        List<? extends String> phoneList = phones.getItems();

        for (String phone : phoneList) {
            String marking = "DEL_" + phone + "_" + System.currentTimeMillis();
            String sql = "UPDATE member SET phone = ? WHERE phone = ?";
            // 배치 삭제
            jdbcTemplate.update(sql, marking, phone);
        }

        log.info("MemberCleanup {}건 삭제 완료", phoneList.size());
    }

    /**
     * 회원 강등 Job
     * @param phones 강등 대상 회원
     */
    public void demote(Chunk<? extends String> phones) {
        List<? extends String> phoneList = phones.getItems();
        for (String phone : phoneList) {
            String sql = "UPDATE member SET grade = 'NORMAL' WHERE phone = ?";
            jdbcTemplate.update(sql, phone);
            log.info("강등 대상 {}", phone);
        }
        log.info("{} 건 강등 완료", phoneList.size());
    }
}
