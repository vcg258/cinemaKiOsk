package com.example.cinemakiosk.controller.aicontroller;

import com.example.cinemakiosk.service.Ai.ETLService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/admin/etl")
@RequiredArgsConstructor
public class EtlController {
    private final ETLService etlService;

    @Operation(summary = "Upload Text File -> ETL PGVector ADD")
    @PostMapping(
            value = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadFile(
            @RequestPart(value = "title", required = false) String title,
            @RequestPart(value = "author", required = false) String author,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        String result = etlService.etlFromFile(title, author, file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Json -> ETL PGVector ADD", description = "Service Test Code -> TEST URL = https://jsonplaceholder.typicode.com/posts")
    @PostMapping("/json")
    public ResponseEntity<String> textJson(@RequestParam String url) throws MalformedURLException {
        String result = etlService.etlFromJsonUrl(url);
        return ResponseEntity.ok(result);
    }
}
