package com.mugen.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "🔒 Este endpoint está protegido!";
    }
}
