package com.example.cinemakiosk.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class ETLService {
    private final ChatModel chatModel; // 키워드 추출 할때 사용
    private final VectorStore vectorStore; // VectorDB에 적용
    @Qualifier("pgJdbcTemplate") private final JdbcTemplate jdbcTemplate; // VectorDB에는 전체 삭제가 없음 새로운 메뉴얼을 넣을때는 전체 삭제를 하고 최신거만 추가하기 위함

    // TODO 직원 메뉴얼이면 HTML추출까지는 필요없어보여서 file과 json만 넣음
    /**
     * 업로드 파일 텍스트 추출 -> 변환 -> 적재
     * @param title 제목
     * @param author 작성자
     * @param file 업로드 파일
     * @return ETL 과정 완료 멘트(?)
     * @throws IOException 잘못된 업로드 파일 예외
     */
    public String etlFromFile (String title, String author, MultipartFile file) throws IOException {
        // 새로 추가시 테이블을 비움
        clearVectorStore();
        log.info("새로운 메뉴얼 업데이트 기존값 제거...");
        clearChatMemory();
        log.info("새로운 메뉴얼 업데이트 사용자 기록 제거...");

        List<Document> documents = extractFromFile(file);
        if (documents == null) {
            throw new IOException(".txt, .json, .pdf, .doc, .docx 파일 중에 하나를 올려주세요.");
        }
        log.info("추출된 documents 수 : {}", documents);

        // 제목이 존재한다면 지정된 title을 넣고 아니면 파일명
        String titleText = StringUtils.hasText(title) ? title : file.getOriginalFilename();
        String authorText = StringUtils.hasText(author) ? author : "미상";

        // 메타 데이터 공통 정보 추가 (틀을 만들어서 추가함)
        for (Document doc : documents) {
            Map<String, Object> metadata = doc.getMetadata();
            metadata.putAll(Map.of(
                    "title", titleText,
//                    "author", authorText, // TODO 새로운 메뉴얼을 등록시 테이블을 비우고 새로운 메뉴얼만 들어가기떄문에 불필요함
                    "source", file.getOriginalFilename()));
        }

        // 청킹
        List<Document> transform = transform(documents);
        log.info("변환된 Document 수 : {}", transform.size());

        vectorStore.add(transform);

        return "올린 문서 추출-변환-적재 완료";
    }

    /**
     * JSON URL 추출 -> 변환 -> 적재
     * @param url JSON URL
     * @return ETL 완료 멘트(?)
     * @throws MalformedURLException 잘못된 JSON URL 예외
     */
    public String etlFromJsonUrl(String url) throws MalformedURLException {
        // 새로 추가시 테이블을 비움
        clearVectorStore();
        log.info("새로운 메뉴얼 업데이트 기존값 제거...");
        clearChatMemory();
        log.info("새로운 메뉴얼 업데이트 사용자 기록 제거...");

        Resource resource = new UrlResource(url);

        JsonReader jsonReader = new JsonReader(resource,
                new JsonMetadataGenerator() {
                    @Override
                    public Map<String, Object> generate(Map<String, Object> jsonMap) {
                        return Map.of(
                                // JSON 테스트 용 (url테스트할때는 author -> userId 주석, 주석해제)
//                                "author", jsonMap.getOrDefault("userId", "미상").toString(),
                                "title", jsonMap.getOrDefault("title", "제목없음"),
                                "author", jsonMap.getOrDefault("author", "미상"),
                                "url", url
                        );
                    }
                });
        List<Document> documents = jsonReader.read();
        log.info("추출된 Documents 수 : {}개", documents);

        List<Document> apply = transform(documents);
        log.info("변환된 Document 수: {}개", apply.size());

        vectorStore.add(apply);
        return "JSON 추출-변환-적재 완료";
    }



    // Helper Method

    /**
     * VectorDB 전체 삭제
     */
    public void clearVectorStore() {
        jdbcTemplate.update("TRUNCATE TABLE rag.vector_store");
    }

    /**
     * ChatMemory 전체 삭제
     */
    public void clearChatMemory() {
        jdbcTemplate.update("TRUNCATE TABLE rag.spring_ai_chat_memory");
    }

    /**
     * 업로드된 파일 내용 바이트로 추출
     * @param file 업로드 파일
     * @return 업로드 파일 내용 바이트로 변경된 리스트
     * @throws IOException 업로드 파일이 이상할경우 예외
     */
    private List<Document> extractFromFile(MultipartFile file) throws IOException {
        // 파일내용 바이트배열로 가져옴
        Resource resource = new ByteArrayResource(file.getBytes());
        log.info("resource : {}", resource);

        List<Document> documents = null;
        // Text 일 경우
        if (file.getContentType().equals("text/plain")) {
            DocumentReader documentReader = new TextReader(resource);
            documents = documentReader.read();
            // PDF 일 경우
        } else if (file.getContentType().equals("application/pdf")) {
            DocumentReader documentReader = new PagePdfDocumentReader(resource);
            documents = documentReader.read();
            // Word 일 경우
        } else if (file.getContentType().contains("wordprocessingml")) {
            DocumentReader documentReader = new TikaDocumentReader(resource);
            documents = documentReader.read();
            // JSON 일 경우
        } else if (file.getContentType().equals("application/json")) {
            JsonReader jsonReader = new JsonReader(resource,
                    new JsonMetadataGenerator() {
                        @Override
                        public Map<String, Object> generate(Map<String, Object> jsonMap) {
                            return Map.of(
                                    "title", jsonMap.getOrDefault("title", "제목없음"),
                                    "author", jsonMap.getOrDefault("author", "미상"),
                                    "source", file.getOriginalFilename()
                            );
                        }
                    });
            documents = jsonReader.read();
        }
        return documents;
    }

    /**
     * 추출 Document 리스트를 작게 분할 키워드로 검색할 수 있게 메타데이터 추가 메소드
     * @param documents 업로드 파일 내용 바이트 리스트
     * @return 텍스트를 토큰 단위로 분할 키워드 메타데이터를 추가한 리스트
     */
    private List<Document> transform(List<Document> documents) {
        List<Document> transformedDocuments = null;

        // Document 기준으로 청크후 분할함 (기본 토큰수 1000)
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(300) // 청크당 최대 토큰 수
                .withMinChunkSizeChars(100) // 청크 간 오버랩 토큰 수 (문맥 유지)
                .withMinChunkLengthToEmbed(5) // 최소 청크 토큰 수
                .withMaxNumChunks(10000) // 최대 청크 토큰 수
                .withKeepSeparator(true) // 문장 경계에서 분할 여부
                .build();
        // 토큰 기준으로 분할한 리스트로 변환
        transformedDocuments = tokenTextSplitter.apply(documents);

//        // 청크마다 키워드 5개 추출 -> 메타데이터 추가 (일단 주석 -> 토큰이 좀 그럼; 10청크 -> 50개 추출)
//        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatModel, 5);
//        // 청크마다 키워드 메타데이터가 추가된 리스트 변환
//        transformedDocuments = keywordMetadataEnricher.apply(transformedDocuments);
        return transformedDocuments;
    }
}
