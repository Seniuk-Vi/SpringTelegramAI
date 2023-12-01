package org.brain.springtelegramai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Value("${gpt.url}")
    private String BASE_URL;
    @Value("${gpt.token}")
    private String TOKEN;

    @Bean
    public WebClient webClientWithTimeout() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer " + TOKEN)
                .build();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer " + TOKEN)
                .build();
    }
}
