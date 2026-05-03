package com.example.cinemakiosk.dto.tmdb.TmdbReleaseDatesDTO;

import lombok.Data;

import java.util.List;

/**
 * TMDB /movie/{id}/release_dates API 응답 DTO
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
 * KR iso_3166_1 항목에서 certification 값을 추출하여 Rating enum으로 변환
 */
@Data
public class TmdbReleaseDatesDTO {
    /** 국가별 등급 정보 목록 */
    private List<CountryRelease> results;

}
