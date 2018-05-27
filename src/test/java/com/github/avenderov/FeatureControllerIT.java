package com.github.avenderov;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FeatureControllerIT {

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
        consulKey = String.format("config/%s/feature/return-no-content", applicationName);
    }

    @Test
    public void shouldReturn200IfToggleIsOff() {
        Response<Boolean> setValueResponse;

        setValueResponse = consulClient.setKVValue(consulKey, Boolean.TRUE.toString());
        assertThat(setValueResponse.getValue()).isTrue();

        final String value = Boolean.FALSE.toString();
        setValueResponse = consulClient.setKVValue(consulKey, value);
        assertThat(setValueResponse.getValue()).isTrue();

        await().atMost(5L, TimeUnit.SECONDS).pollInterval(500L, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(environment.getProperty("feature.return-no-content"), value));

        final ResponseEntity<Void> responseEntity =
                restTemplate.exchange("/feature-test", HttpMethod.GET, HttpEntity.EMPTY, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturn204IfToggleIsOn() {
        Response<Boolean> setValueResponse;

        setValueResponse = consulClient.setKVValue(consulKey, Boolean.FALSE.toString());
        assertThat(setValueResponse.getValue()).isTrue();

        final String value = Boolean.TRUE.toString();
        setValueResponse = consulClient.setKVValue(consulKey, value);
        assertThat(setValueResponse.getValue()).isTrue();

        await().atMost(5L, TimeUnit.SECONDS).pollInterval(500L, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(environment.getProperty("feature.return-no-content"), value));

        final ResponseEntity<Void> responseEntity =
                restTemplate.exchange("/feature-test", HttpMethod.GET, HttpEntity.EMPTY, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
