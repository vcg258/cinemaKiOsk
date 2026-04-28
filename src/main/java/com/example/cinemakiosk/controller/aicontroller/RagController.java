package com.example.cinemakiosk.controller.aicontroller;

import com.example.cinemakiosk.service.Ai.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/rag")
@RequiredArgsConstructor
public class RagController {
    private final RagService ragService;

    // DTO record를 이용해서 생성
    public record RagRequest(String question, String title, String conversationId) {}

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody RagRequest ragRequest) {
        String chat = ragService.chat(
                ragRequest.question(), 0.3, ragRequest.title(), ragRequest.conversationId()
        );
        return ResponseEntity.ok().body(chat);
    }
}
