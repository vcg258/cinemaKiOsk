package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "맴버 전체 조회")
    @GetMapping("/list")
    public ResponseEntity<List<MemberDTO>> getMemberList(){
        return ResponseEntity.ok(memberService.getMembersAll());
    }

    @Operation(summary = "지정 회원 전체 포인트 내역")
    @GetMapping("/{phone}/point-list")
    public ResponseEntity<List<PointHistoryDTO>> getPointHistoryList(@PathVariable String phone){
        return ResponseEntity.ok(memberService.getMembersAllLog(phone));
    }

    @Operation(summary = "맴버 단일 조회")
    @GetMapping("/{phone}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable String phone){
        return ResponseEntity.ok(memberService.getMember(phone));
    }
}
