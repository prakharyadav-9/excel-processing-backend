package com.py.acceptexcelsvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AcceptExcelSvcDefault {
    @GetMapping("")
    public String getTest() {
        return "Spring Application is up";
    }
}
