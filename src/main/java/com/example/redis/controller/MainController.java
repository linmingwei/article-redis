package com.example.redis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Auther: mingweilin
 * @Date: 1/10/2020 14:08
 * @Description:
 */
@RestController
public class MainController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
