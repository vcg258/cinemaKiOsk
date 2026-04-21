package com.example.cinemakiosk.exception.enums;

import lombok.Getter;

@Getter
public enum TokenError {
    UNACCEPT(401, "Token is null or too short"), // 토큰이 비어 있거나 너무 짧음
    BADTYPE(401, "Token type Bearer"), // 토큰타입이 Bearer가 아님
    MALFORM(403, "Malformed Token"), // 토큰 형식 자체가 잘못됨
    BADSIGN(403, "BadSignatured Token"), // 서명이 위조됨
    EXPIRED(403, "Expired Token"); // 토큰이 만료됨

    private final int status;
    private final String msg;

    TokenError(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
