package com.example.cinemakiosk.service.ai;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
public class ETLService {
    private final ChatModel chatModel; // 키워드 추출 할때 사용
    private final VectorStore vectorStore; // VectorDB에 적용
    private final JdbcTemplate jdbcTemplate; // VectorDB에는 전체 삭제가 없음 새로운 메뉴얼을 넣을때는 전체 삭제를 하고 최신거만 추가하기 위함

    public ETLService(ChatModel chatModel,
                      VectorStore vectorStore,
                      @Qualifier("pgJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    // ETL추출을 할때 title 키워드를 지정하기 위해 @title(@title:) = title로 형식 지정 (싱글턴)
    private static final Pattern TITLE_PATTERN = Pattern.compile("@title:?\\s*(.+?)\\s*$", Pattern.MULTILINE);

    /**
     * 업로드 파일 텍스트 추출 -> 변환 -> 적재
     * @param title 제목
     * @param file 업로드 파일
     * @return ETL 과정 완료 멘트(?)
     * @throws IOException 잘못된 업로드 파일 예외
     */
    public String etlFromFile (String title, MultipartFile file) throws IOException {
        // 새로 추가시 테이블을 비움
        initializeStores();

        // 타입에 맞게 파일 내용을 바이트로 추출
        List<Document> documents = extractFromFile(file);
        if (documents == null) {
            throw new IOException(".txt, .json, .pdf, .doc, .docx 파일 중에 하나를 올려주세요.");
        }
        log.info("추출된 documents 수 : {}", documents.size());

        // 제목이 존재한다면 지정된 title을 넣고 아니면 파일명
        String titleText = StringUtils.hasText(title) ? title : file.getOriginalFilename();
        String contentType = file.getContentType();

        // PDF(파일 형식안에 넣어야함 얘는), JSON은 title을 이미 지정 해놨기때문에 제외함
        boolean isSectionable = contentType.equals("text/plain") || contentType.contains("wordprocessingml");
        List<Document> finalDocuments;

        // txt파일 일 경우
        if (isSectionable) {
            StringBuilder sb = new StringBuilder();
            for (Document doc : documents) {
                sb.append(doc.getText()).append("\n");
            }
            String rawText = sb.toString();

            if (TITLE_PATTERN.matcher(rawText).find()) {
                finalDocuments = parseByTitleMarker(rawText, file.getOriginalFilename());
                log.info("@title 구분자 있음, 분리 : {}개",  finalDocuments.size());
            } else {
                documents.forEach(doc -> doc.getMetadata().putAll(
                        Map.of("title", titleText, "source", file.getOriginalFilename())
                ));
                finalDocuments = documents;
                log.info("@title 구분자 없음");
            }
        } else {
            documents.forEach(doc -> doc.getMetadata().putAll(
                    Map.of("title", titleText, "source", file.getOriginalFilename())
            ));
            finalDocuments = documents;
        }

        // 청킹
        List<Document> transform = transform(finalDocuments);
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
        initializeStores();

        Resource resource = new UrlResource(url);

        JsonReader jsonReader = new JsonReader(resource,
                new JsonMetadataGenerator() {
                    @Override
                    public Map<String, Object> generate(Map<String, Object> jsonMap) {
                        return Map.of(
                                // JSON 테스트 용 (url테스트할때는 userId 주석해제)
//                                "author", jsonMap.getOrDefault("userId", "미상").toString(),
                                "title", jsonMap.getOrDefault("title", "제목없음"),
                                "url", url
                        );
                    }
                });
        List<Document> documents = jsonReader.read();
        log.info("추출된 Documents 수 : {}개", documents.size());

        List<Document> apply = transform(documents);
        log.info("변환된 Document 수: {}개", apply.size());

        vectorStore.add(apply);
        return "JSON 추출-변환-적재 완료";
    }



    // Helper Method

    /**
     * 구분자 @title을 기준으로 분리 하는 메서드
     * @param rawText title 제목
     * @param sourceFileName source 파일명
     * @return 구분자를 기준으로 title을 추가한 Document
     */
    private List<Document> parseByTitleMarker(String rawText, String sourceFileName) {
        // split 사용을 위한 배열 사용
        String[] blocks = rawText.split("(?=@title)");
        List<Document> result = new ArrayList<>();

        for (String block : blocks) {
            String trimmed = block.strip();
            if (trimmed.isEmpty()) continue;

            Matcher matcher = TITLE_PATTERN.matcher(trimmed);
            if (!matcher.find()) continue;

            String sectionTitle = matcher.group(1).strip();
            String content = trimmed.substring(matcher.end()).strip();

            if (content.isEmpty()) continue;

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", sectionTitle);
            metadata.put("source", sourceFileName);

            result.add(new Document(content, metadata));
        }

        return result;
    }

    /**
     * 통합 제거
     */
    public void initializeStores() {
        clearVectorStore();
        log.info("새로운 메뉴얼 업데이트 기존값 제거...");
        clearChatMemory();
        log.info("새로운 메뉴얼 업데이트 사용자 기록 제거...");
    }

    /**
     * VectorDB 전체 삭제
     */
    public void clearVectorStore() {
        jdbcTemplate.update("TRUNCATE TABLE rag.public.vector_store");
    }

    /**
     * ChatMemory 전체 삭제
     */
    public void clearChatMemory() {
        jdbcTemplate.update("TRUNCATE TABLE rag.public.spring_ai_chat_memory");
    }

    /**
     * 업로드된 파일 내용 바이트로 추출
     * @param file 업로드 파일
     * @return 업로드 파일 내용 바이트로 변경된 리스트
     * @throws IOException 업로드 파일이 이상할경우 예외
     */
    private List<Document> extractFromFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IOException("잘못된 파일 타입");
        }

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
        // Document 기준으로 청크후 분할함 (기본 토큰수 1000)
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(300) // 청크당 최대 토큰 수
                .withMinChunkSizeChars(100) // 청크 간 오버랩 토큰 수 (문맥 유지)
                .withMinChunkLengthToEmbed(5) // 최소 청크 토큰 수
                .withMaxNumChunks(10000) // 최대 청크 토큰 수
                .withKeepSeparator(true) // 문장 경계에서 분할 여부
                .build();
        // 토큰 기준으로 분할한 리스트로 변환
        List<Document> transformedDocuments = tokenTextSplitter.apply(documents);

        // 청크마다 키워드 5개 추출 -> 메타데이터 추가 (일단 주석 -> 토큰이 좀 그럼; 10청크 -> 50개 추출)
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatModel, 5);
        // 청크마다 키워드 메타데이터가 추가된 리스트 변환
        transformedDocuments = keywordMetadataEnricher.apply(transformedDocuments);
        return transformedDocuments;
    }
}
