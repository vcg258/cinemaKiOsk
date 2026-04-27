package com.example.cinemakiosk.dto.Tmdb.TmdbReleaseDatesDTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CountryRelease {

    /** 국가 코드 (e.g. "KR", "US") */
    @JsonProperty("iso_3166_1")
    private String iso31661;

    /** 해당 국가의 개봉 정보 목록 */
    @JsonProperty("release_dates")
    private List<ReleaseDate> releaseDates;
}