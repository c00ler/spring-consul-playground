package com.github.avenderov.controller;

import com.github.avenderov.controller.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/static/hello")
@Slf4j
public class StaticHelloController {

    @Value("${dynamic.name:unknown}")
    private String name;

    @PostConstruct
    void logInitialValue() {
        log.info("Static controller created with initial value: {}", name);
    }

    @GetMapping
    public MessageDto hello() {
        return new MessageDto(String.format("Hello, %s!", name));
    }

}
