package com.petshop.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatusController {

    @GetMapping("/test")
    public String test() {
        return "status";
    }
}
