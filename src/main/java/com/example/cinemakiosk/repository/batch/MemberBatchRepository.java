//package com.example.cinemakiosk.repository.batch;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//
//@Repository
//@RequiredArgsConstructor
//public class MemberBatchRepository {
//    private final JdbcTemplate jdbcTemplate;
//
//    // 2년간 point_history 기록이 없는 회원 phone 조회
//    public List<String> findInactiveMemberPhones() {
//        return jdbcTemplate.queryForList("""
//                SELECT m.phone
//                FROM member m
//                WHERE NOT EXISTS (
//                    SELECT 1
//                    FROM point_history p
//                    WHERE p.phone = m.phone
//                      AND p.create_at >= NOW() - INTERVAL 2 YEAR
//                """, String.class);
//    }
//
//    // 회원 삭제 (point_history는 CASCADE로 자동 삭제)
//    public void deleteByPhones(List<String> phones) {
//        jdbcTemplate.batchUpdate(
//                "DELETE FROM member WHERE phone = ?",
//                phones,
//                phones.size(),
//                (ps, phone) -> ps.setString(1, phone)
//        );
//    }
//}
//}
