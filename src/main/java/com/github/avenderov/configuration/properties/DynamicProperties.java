package com.github.avenderov.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "dynamic")
@Data
public class DynamicProperties {

    private String name;

}
