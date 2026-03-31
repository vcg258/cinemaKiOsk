package com.example.cinemakiosk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SpaController — React SPA 라우팅 fallback
 *
 * React Router는 클라이언트 사이드 라우팅을 사용하기 때문에,
 * /movie/list, /booking/seat 같은 URL을 브라우저에서 직접 접근하거나
 * 새로고침하면 Spring Boot가 해당 경로를 모르고 404를 반환함.
 *
 * 이 컨트롤러는 API, WebSocket, 정적 파일(.js, .css, 이미지 등)을 제외한
 * 모든 GET 요청을 index.html 로 forward해서 React Router가 처리하도록 함.
 *
 * 동작 원리:
 *   브라우저 → GET /booking/seat
 *   → Spring Boot: SpaController.forward() 매칭
 *   → forward:/index.html (static/index.html 반환)
 *   → React Router가 /booking/seat 경로를 클라이언트에서 처리
 */
@Controller
public class SpaController {

    /**
     * SPA fallback: 정적 파일(.js, .css 등) 요청이 아닌 모든 GET → index.html 로 forward
     *
     * 패턴 설명:
     *   [^\\.]*  →  '.' 이 없는 경로 세그먼트만 매칭 (확장자 있는 파일 경로는 제외)
     *   예: /movie/list → 매칭 O    /assets/index.js → 매칭 X
     *
     * ⚠️ Spring MVC URL 매칭 우선순위:
     *   구체적인 경로가 항상 먼저 매칭됨.
     *   → /api/**, /view/** 같은 @RestController 경로는 이 fallback보다 먼저 매칭됨.
     *   → 따라서 API 요청이 여기로 오는 일은 없음.
     *
     * 최대 4단계 경로까지 커버 (실제 라우트: /admin/management/movie/list 등)
     */
    @GetMapping(value = {
        "/",
        "/{a:[^\\.]*}",
        "/{a:[^\\.]*}/{b:[^\\.]*}",
        "/{a:[^\\.]*}/{b:[^\\.]*}/{c:[^\\.]*}",
        "/{a:[^\\.]*}/{b:[^\\.]*}/{c:[^\\.]*}/{d:[^\\.]*}"
    })
    public String forward() {
        // static/index.html 을 클라이언트에 반환
        // React가 로드된 후 React Router가 URL에 맞는 컴포넌트를 렌더링
        return "forward:/index.html";
    }
}
