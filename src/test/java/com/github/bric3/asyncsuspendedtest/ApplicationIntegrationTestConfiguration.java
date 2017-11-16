package com.github.bric3.asyncsuspendedtest;

import okhttp3.OkHttpClient;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@TestConfiguration
@Import(AsyncSuspendedExperimentApplication.class)
public class ApplicationIntegrationTestConfiguration {
    @Bean
    @Lazy
    OkHttpClient httpClient(@LocalServerPort int port) {
        return new OkHttpClient.Builder().build();
    }
}
