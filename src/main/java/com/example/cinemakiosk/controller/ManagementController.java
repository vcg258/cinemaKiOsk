package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.service.DiscountPolicyService;
import com.example.cinemakiosk.service.MovieService;
import com.example.cinemakiosk.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/manegement")
@RequiredArgsConstructor
public class ManagementController {
    private final TheaterService theaterService;
    private final MovieService movieService;
    private final DiscountPolicyService discountPolicyService;
}
