package org.brain.springtelegramai;

import org.brain.springtelegramai.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SpringTelegramAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTelegramAiApplication.class, args);
    }

}
