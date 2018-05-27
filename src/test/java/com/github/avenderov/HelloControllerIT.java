package com.github.avenderov;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.github.avenderov.controller.dto.MessageDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerIT {

    @Autowired
    private ConsulClient consulClient;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;

    private String consulKey;

    @Before
    public void beforeEach() {
        consulKey = String.format("config/%s/dynamic/name", applicationName);
    }

    @Test
    public void dynamicControllerShouldRefreshTheValue() {
        final String value = UUID.randomUUID().toString();
        final Response<Boolean> setValueResponse = consulClient.setKVValue(consulKey, value);
        assertThat(setValueResponse.getValue()).isTrue();

        await().atMost(5L, TimeUnit.SECONDS).pollInterval(500L, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(environment.getProperty("dynamic.name"), value));

        final MessageDto response = restTemplate.getForObject("/dynamic/hello", MessageDto.class);
        assertThat(response.getMessage()).endsWith(value + "!");
    }

    @Test
    public void staticControllerShouldNotRefreshTheValue() {
        final MessageDto beforeChange = restTemplate.getForObject("/static/hello", MessageDto.class);

        final String value = UUID.randomUUID().toString();
        final Response<Boolean> setValueResponse = consulClient.setKVValue(consulKey, value);
        assertThat(setValueResponse.getValue()).isTrue();

        await().atMost(5L, TimeUnit.SECONDS).pollInterval(500L, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(environment.getProperty("dynamic.name"), value));

        final MessageDto afterChange = restTemplate.getForObject("/static/hello", MessageDto.class);
        assertThat(afterChange.getMessage()).isEqualTo(beforeChange.getMessage());
    }

}
