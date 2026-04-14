package com.example.cinemakiosk.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Map;

@Log4j2
@Component
public class JwtUtil {

    @Value("${jwt.cinema.scret.key}")
    private String key;

    // 서명키 변환 (내부에서만 사용하도록 분리함)
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String generateToken(Map<String, Object> valueMap, int days) {

        return Jwts.builder()
                .header()
                    .type("JWT")
                    .and()
                .claims(valueMap) // PayLoad (사용자의 데이터가 들어감)
                .issuedAt(Date.from(ZonedDateTime.now().toInstant())) // 어디 나라인지 표시하기때문에 사용함 (사실 굳이긴해)
                .expiration(Date.from(ZonedDateTime.now().plusDays(days).toInstant()))
                .signWith(getSigningKey()) // "alg", "HS256"를 자동으로 넣어주기때문에 수동으로 안넣음
                .compact(); // 위 모든 설정 합치고 JWT 문자열로 만듬
    }

    /**
     * 토큰 검증
     * @param token 토큰
     * @return 서명키, PayLoad 모든 검증이 통과된 토큰
     * @throws JwtException 검증 실패
     */
    public Map<String, Object> validateToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // 서명키 검증
                .build()
                .parseSignedClaims(token) // PayLoad 데이터 검증 (위조된건지 만료인지)
                .getPayload(); // 위 조건 통과? 데이터 사용
    }

}