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

//    @Builder.Default
//    private int page = 1; // 페이지 번호
//
//    @Builder.Default
//    private int size = 10; // 한페이지당 개수

    private String type; // 검색 종류 ex) t, w, tc, tcw

    private String keyword; // 검색

    public String[] getTypes() {
        if (this.type == null || this.type.isEmpty()) {
            return null;
        }
        return this.type.split("");
    }

//    public Pageable getPageAble(String prop) {
//        return PageRequest.of(this.page - 1, this.size, Sort.by(prop).descending());
//    }


    // 링크 조합
    private String link;
    public String getLink() {
        if (this.link == null) {
            StringBuilder stringBuilder = new StringBuilder(); // 사용이유 문자열로 해도 되지만 사용하면 메모리 사용량 증가하기 때문
//            stringBuilder.append("page=").append(this.page);
//            stringBuilder.append("&size=").append(this.size);

            // type
            if (this.type != null && !this.type.isEmpty()) {
                stringBuilder.append("&type=").append(this.type);
            }

            if (this.type != null && !this.keyword.isEmpty()) {
                stringBuilder.append("&keyword=").append(URLEncoder.encode(this.keyword, StandardCharsets.UTF_8));
            }
            this.link = stringBuilder.toString();
        }
        return this.link;
    }
}
