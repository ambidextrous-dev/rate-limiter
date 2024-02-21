package com.ambidextrous.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/hi")
    public String greeting() {
        return "Hello";
    }


}
