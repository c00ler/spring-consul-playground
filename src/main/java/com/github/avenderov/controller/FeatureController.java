package com.github.avenderov.controller;

import com.github.avenderov.togglz.Features;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feature-test")
public class FeatureController {

    @GetMapping
    public ResponseEntity<Void> test() {
        if (Features.RETURN_NO_CONTENT.isActive()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().build();
    }

}
