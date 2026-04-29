package com.example.cinemakiosk.controller.aicontroller;

import com.example.cinemakiosk.service.Ai.RagService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "RAG chat", description = "question = 질문, title = 제목, conversationID = 사용자 아이디")
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody RagRequest ragRequest) {
        String chat = ragService.chat(
                ragRequest.question(), 0.3, ragRequest.title(), ragRequest.conversationId()
        );
        return ResponseEntity.ok().body(chat);
    }
}
