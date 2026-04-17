package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.domain.enums.Type;
import com.example.cinemakiosk.dto.*;
import com.example.cinemakiosk.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@RestController // @Controller + @ResponseBody 합쳐진 것
@RequiredArgsConstructor
@RequestMapping(value = "/api/payment")
public class PaymentController {

    private final ObjectMapper objectMapper; // 스프링이 자동으로 주입해주는 JSON 변환기
    private final ReservationService reservationService; //확정된 예매 등록을 위해서 작성
    private final PaymentDetailsService paymentDetailsService; //결제 내역 등록을 위해서 작성.
    private final MemberService memberService; //포인트 관리를 위해서 작성.
    private final BonusPolicyService bonusPolicyService; //보너스 적립 비율 확인을 위해 사용
    private final ScheduleService scheduleService; // 스케쥴 정보를 그냥 받아오는게 빠를듯.
    private final DiscountPolicyService discountPolicyService;
    private final RefundService refundService;

    /**
     * 결제를 확정짓는 메서드
     * @param jsonBody
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/confirm")
    public ResponseEntity<JsonNode> confirmPayment(@RequestBody String jsonBody) throws Exception {
        log.info("결제 컨펌 로직 시작");
        log.info("jsonBody : {}",jsonBody);

        // 1. 파싱 (Jackson 사용)
        JsonNode requestData = objectMapper.readTree(jsonBody);
        String payType = requestData.path("payType").asText(); // 기본값 CARD
        String orderId = requestData.path("orderId").asText();
        String amountStr = requestData.path("amount").asText("0");
        long amount = Long.parseLong(amountStr);

        String PAY_TYPE_CARD = "CARD";

        JsonNode responseResult = null;
        int statusCode = 200;


        if (PAY_TYPE_CARD.equalsIgnoreCase(payType)) {
            log.error("경계2");
            // 1. 토스 결제 승인 로직
            String paymentKey = requestData.get("paymentKey").asText();
            responseResult = confirmTossPayment(orderId, amount, paymentKey);

            // 토스 응답이 200이 아니면 실패 처리 (예외 던지거나 에러 리턴)
            if (responseResult.has("code") && !responseResult.has("status")) {
                return ResponseEntity.status(400).body(responseResult);
            }
        } else {
            log.error("경계3");
            // 2. 포인트 전액 결제 로직
            log.info("포인트 전액 결제 처리: {}", orderId);
            ObjectNode successNode = objectMapper.createObjectNode();
            successNode.put("status", "DONE");
            successNode.put("orderId", orderId);
            successNode.put("method", "POINT");
            responseResult = successNode;
        }
        log.error("경계4");

        // 3. 공통 DB 저장 로직 실행
        savePaymentInfo(requestData, amount);

        log.error("경계5");
        return ResponseEntity.status(statusCode).body(responseResult);
    }

    /**
     * 토스 API 승인 요청 메서드
     */
    private JsonNode confirmTossPayment(String orderId, long amount, String paymentKey) throws Exception {
        String widgetSecretKey = "test_sk_Ba5PzR0ArnOZp4xwZ16N8vmYnNeD";
        String authorizations = "Basic " + Base64.getEncoder().encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        ObjectNode obj = objectMapper.createObjectNode();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(obj.toString().getBytes(StandardCharsets.UTF_8));
        }

        int code = connection.getResponseCode();
        try (InputStream is = (code == 200) ? connection.getInputStream() : connection.getErrorStream()) {
            return objectMapper.readTree(is);
        }
    }

    /**
     * DB 저장 공통 로직 (Reservation, Payment, Point History)
     */
    @Transactional
    protected void savePaymentInfo(JsonNode requestData, long amount) throws Exception {
        log.info("데이터 저장 로직 시작.");
        String orderId = requestData.get("orderId").asText();
        String phone = requestData.get("phone").asText();
        String paymentKey = requestData.get("paymentKey").asText();
        long bonusPolicyId = requestData.get("bonusPolicyId").asLong();
        long usePoint = requestData.get("usePoint").asLong();
        Long scheduleIdValue = requestData.path("scheduleId").path("scheduleId").asLong();
        log.error("경계1");
        // 좌석 정보 파싱
        List<ReservationSeatDTO> seats = new ArrayList<>();

        JsonNode seatsNode = requestData.get("seats");
        if (seatsNode != null && seatsNode.isArray()) {
            for (JsonNode seat : seatsNode) {
                seats.add(ReservationSeatDTO.builder().seatNumber(seat.asText()).build());
            }
        }
        log.error("경계2 {}", seats);
        ScheduleDTO schedule = scheduleService.getScheduleDTO(scheduleIdValue);
        MemberDTO member = memberService.getMember(phone);
        BonusPolicyDTO bonusPolicy = bonusPolicyService.getBonusPolicy(bonusPolicyId);
        CouponDTO couponNum = discountPolicyService.getCoupon(requestData.path("couponNum").asText(""));
        log.error("경계3");

        // 1. 예매 등록
        ReservationDetailsDTO reservation = ReservationDetailsDTO.builder()
                .id(orderId)
                .schedule(schedule)
                .phone(member)
                .seats(seats)
                .returned(false)
                .createAt(LocalDateTime.now())
                .build();

        log.error("경계4");
        reservationService.create(reservation);
        log.error("경계5");
        ReservationDetailsDTO reservationDetailsDTO = reservationService.read(orderId);

        log.error("경계6");
        // 2. 결제 상세 등록
        PaymentDetailsDTO payment = PaymentDetailsDTO.builder()
                .id(orderId)
                .reservation(reservationDetailsDTO)
                .bonusPolicy(bonusPolicy)
//                .couponNum(couponNum)
                .couponNum(null)
                .cost(amount)
                .createAt(LocalDateTime.now())
                .usePoint(usePoint)
                .status(Status.PAY)
                .paymentKey(paymentKey)
                .build();
        log.error("경계7");
        paymentDetailsService.create(payment);
        log.error("경계8");

        // 3. 포인트 처리
        int earnPoint = (int) (amount * bonusPolicy.getGiveValue() / 100);

        log.error("경계9");
        // 사용 내역 - 통과
        if (usePoint > 0) {
            memberService.pointHistoryCreate(PointHistoryDTO.builder()
                    .paymentId(orderId).phone(phone).type(Type.USE).amountPoint((int) usePoint)
                    .createAt(LocalDateTime.now()).title("영화 예매 포인트 사용")
                    .build());
            log.info("Point log");
        }

        log.error("경계10");
        // 적립 내역 - 걸림
        if (earnPoint > 0) {
            memberService.pointHistoryCreate(PointHistoryDTO.builder()
                    .paymentId(orderId).phone(phone).type(Type.EARN).amountPoint(earnPoint)
                    .createAt(LocalDateTime.now()).title("영화 예매 포인트 적립")
                    .build());
            log.info("Point Earn {}", earnPoint);

        }
        log.error("경계11");
        // 멤버 실제 포인트 업데이트
        log.info("멤버 확인 : {}",member);
//        member.setPoint(member.getPoint() - (int) usePoint + earnPoint);
        log.info("변경 멤버 확인 : {}", member);


        log.info("DB 저장 및 포인트 갱신 완료: 주문번호 {}", orderId);
    }

    /**
     * 조회를 위한 값
     * @param no
     * @return
     */
    @GetMapping("/read/{uuid}")
    public ResponseEntity<PaymentDetailsDTO> readOne(@PathVariable("uuid") String no){
        log.info("찾으려는 uuid : {}", no);
        PaymentDetailsDTO paymentDetailsDTO = paymentDetailsService.read(no);
        log.info("조회된 결제 내역 : {}", paymentDetailsDTO);
        return ResponseEntity.ok(paymentDetailsDTO);
    }

    @PostMapping("/refund")
    public ResponseEntity<Void> refund(@RequestBody String jsonBody) throws JsonProcessingException {
        //1. payment값이 필요함
        //2. 결제 내역 정보가 필요함
        log.info("jsonBody : {}",jsonBody);

        RestTemplate restTemplate = new RestTemplate();

        log.error("경계");
        // 1. 파싱 (Jackson 사용)
        JsonNode requestData = objectMapper.readTree(jsonBody);
        String paymentKey = requestData.get("paymentKey").asText();
        String paymentId = requestData.get("paymentId").asText();

        //환불처리
        // 1. URL 설정
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        // 2. 헤더 설정 (Authorization & Content-Type)
        HttpHeaders headers = new HttpHeaders();
        // Authorization: Basic {Base64 인코딩된 시크릿키}

        String secretKey = "test_sk_Ba5PzR0ArnOZp4xwZ16N8vmYnNeD";// 1. 테스트 시크릿 키
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes()); // 2. 키 뒤에 콜론(:)을 더하고 Base64로 변환

        headers.set("Authorization", "Basic " + encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. 바디(데이터) 설정
        Map<String, String> map = new HashMap<>();
        map.put("cancelReason", "구매자가 취소를 원함");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);

        // 4. API 호출 및 응답 받기
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            log.info("{} : {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("취소 성공: " + response.getBody());

                //환불처리
                refundService.refund(paymentId);

            }
        } catch (Exception e) {
            System.err.println("취소 실패: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}