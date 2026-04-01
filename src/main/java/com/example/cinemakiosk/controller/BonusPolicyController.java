package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.service.BonusPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/Bonus")
public class BonusPolicyController {

    BonusPolicyService bonusPolicyService;

    @GetMapping("/read")
    public void getBonusPolicy(Model model) {

        model.addAttribute("bonusList", bonusPolicyService.getBonusPolicies());

    }
}
