package com.example.cinemakiosk.controller.AiController;

import com.example.cinemakiosk.service.Ai.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rag")
@RequiredArgsConstructor
public class RagController {
    private final RagService ragService;

    @PostMapping(
            value = "/chat",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String chat(
            @RequestParam String question,
            @RequestParam String source,
            @RequestParam String conversationId
    ) {
        return ragService.chat(question, 0.7, source, conversationId);
    }
}
