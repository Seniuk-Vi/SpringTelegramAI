package org.brain.springtelegramai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(String secret, Integer lifetime, String issuer) {
}
