package com.github.avenderov.controller;

import com.github.avenderov.configuration.properties.DynamicProperties;
import com.github.avenderov.controller.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/dynamic/hello")
@Slf4j
@RequiredArgsConstructor
public class DynamicHelloController {

    private final DynamicProperties dynamicProperties;

    @PostConstruct
    void logInitialValue() {
        log.info("Dynamic controller created with initial value: {}", dynamicProperties.getName());
    }

    @GetMapping
    public MessageDto hello() {
        if (dynamicProperties.getName() == null) {
            return new MessageDto();
        }

        return new MessageDto(String.format("Hello, %s!", dynamicProperties.getName()));
    }

}
