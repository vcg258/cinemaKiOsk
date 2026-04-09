package com.example.cinemakiosk.domain.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Rating {
    ALL,
    TWELVE,
    FIFTEEN,
    NINETEEN;

    /**
     * JSON 직렬화 시 enum 이름(TWELVE) 대신 실제 등급 문자열("12") 반환
     * - @JsonValue : Jackson이 이 메서드 반환값으로 직렬화
     */
    @JsonValue
    public String getConversion() {
        return switch (this) {
            case ALL -> "ALL";
            case TWELVE -> "12";
            case FIFTEEN -> "15";
            case NINETEEN -> "19";
        };
    }

    /**
     * JSON 역직렬화 시 "12" → TWELVE 로 변환
     * - @JsonCreator : Jackson이 요청 바디 파싱 시 이 메서드 사용
     */
    @JsonCreator
    public static Rating fromConversion(String value) {
        return switch (value) {
            case "ALL" -> ALL;
            case "12" -> TWELVE;
            case "15" -> FIFTEEN;
            case "19" -> NINETEEN;
            default -> throw new IllegalArgumentException("Unknown rating: " + value);
        };
    }

}

