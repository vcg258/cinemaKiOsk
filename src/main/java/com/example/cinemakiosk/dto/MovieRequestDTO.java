package com.example.cinemakiosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequestDTO {

    private String type; // 검색 종류 ex) t, w, tc, tcw

    private String keyword; // 검색

    // 링크 조합
    private String link;
}
