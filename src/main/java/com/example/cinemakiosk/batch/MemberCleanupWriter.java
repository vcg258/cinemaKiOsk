package com.example.cinemakiosk.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Log4j2
public class MemberCleanupWriter implements ItemWriter<String> {

    private final JdbcTemplate jdbcTemplate;

    public void write(Chunk<? extends String> phones) {
        List<? extends String> phoneList = phones.getItems();

        // 삭제 전 백업
        backupBeforeDelete(phoneList);

        // 배치 삭제
        jdbcTemplate.batchUpdate(
                "DELETE FROM member WHERE phone = ?",
                phoneList,
                phoneList.size(),
                (ps, phone) -> ps.setString(1, phone)
        );

        log.info("MemberCleanup {}건 삭제 완료", phoneList.size());
    }


    // 백업
    private void backupBeforeDelete(List<? extends String> phones) {
        String inClause = phones.stream()
                .map(p -> "?")
                .collect(Collectors.joining(","));

        log.info("phoneSize = {}", inClause);
        jdbcTemplate.update(
                "INSERT INTO member_cleanup_log (phone, create_at, deleted_at) " + // 1. create_at 추가
                        "SELECT phone, create_at, NOW() " +                               // 2. 원본 데이터에서 가져오기
                        "FROM member WHERE phone IN (" + inClause + ")",
                phones.toArray()
        );
    }
}
