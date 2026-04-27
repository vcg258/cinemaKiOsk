package com.example.cinemakiosk.dto.Tmdb.TmdbReleaseDatesDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReleaseDate {

    /**
     * 관람 등급 인증 문자열
     * KR 기준: "All" / "" / "12" / "15" / "18" / "19"
     */
    private String certification;

    /** 개봉일 (ISO 8601) */
    @JsonProperty("release_date")
    private String releaseDate;
}
