package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.service.ScheduleService;
import com.example.cinemakiosk.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final ScheduleService scheduleService;
}
