package com.example.cinemakiosk.exception.enums;

import lombok.Getter;

@Getter
public enum ErrorCase {
    NO_ACCESS, // 액세스 토큰이 없음
    BAD_ACCESS, // 액세스 토큰이 유효하지 않음
    NO_REFRESH, // 리프레시 토큰이 없음
    OLD_REFRESH, // 리프레시 토큰이 만료됨
    BAD_REFRESH // 리프레시 토큰이 유효하지 않음
}
