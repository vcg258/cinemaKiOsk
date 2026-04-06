package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Operation(summary = "스케줄 등록")
    @PostMapping("")
    public ResponseEntity<Void> addSchedule(@RequestBody ScheduleDTO scheduleDTO){
        scheduleService.createSchedule(scheduleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "스케줄 수정", description = "id, endAt, activation은 X")
    @PutMapping("")
    public ResponseEntity<Void> modifySchedule(@RequestBody ScheduleDTO scheduleDTO){
        scheduleService.updateSchedule(scheduleDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스케줄 상태 변경")
    @PatchMapping("/activation")
    public ResponseEntity<Void> modifyActiveSchedule(@RequestBody ActivationRequest request){
        scheduleService.updateActivation(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스케줄 삭제")
    @DeleteMapping("/schedule/del")
    public ResponseEntity<Void> deleteSchedule(@RequestParam List<Long> ids){
        scheduleService.deleteSchedule(ids);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스케줄 전체 조회")
    @GetMapping("/list")
    public ResponseEntity<List<ScheduleDTO>> getScheduleList(){
        return ResponseEntity.ok(scheduleService.getScheduleList());
    }

    @Operation(summary = "지정 영화에 해당하는 전체 스케줄 조회")
    @GetMapping("{id}/movie")
    public ResponseEntity<List<ScheduleDTO>> getScheduleByMovie(@PathVariable Long id){
        return ResponseEntity.ok(scheduleService.getScheduleListByMovie(id));
    }

    @Operation(summary = "스케줄 단일 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id){
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }
}
