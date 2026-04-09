package com.example.cinemakiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * TMDB /movie/{id}/release_dates API 응답 DTO
 *
 * 응답 구조:
 * {
 *   "results": [
 *     {
 *       "iso_3166_1": "KR",
 *       "release_dates": [
 *         { "certification": "15", "release_date": "...", ... }
 *       ]
 *     }
 *   ]
 * }
 *
 * KR iso_3166_1 항목에서 certification 값을 추출하여 Rating enum으로 변환
 */
@Data
public class TmdbReleaseDatesDTO {

    /** 국가별 등급 정보 목록 */
    private List<CountryRelease> results;

    @Data
    public static class CountryRelease {

        /** 국가 코드 (e.g. "KR", "US") */
        @JsonProperty("iso_3166_1")
        private String iso31661;

        /** 해당 국가의 개봉 정보 목록 */
        @JsonProperty("release_dates")
        private List<ReleaseDate> releaseDates;
    }

    @Data
    public static class ReleaseDate {

        /**
         * 관람 등급 인증 문자열
         * KR 기준: "All" / "" / "12" / "15" / "18" / "19"
         */
        private String certification;

        /** 개봉일 (ISO 8601) */
        @JsonProperty("release_date")
        private String releaseDate;
    }
}
