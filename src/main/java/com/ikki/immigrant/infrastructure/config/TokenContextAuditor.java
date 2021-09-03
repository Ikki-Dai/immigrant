package com.ikki.immigrant.infrastructure.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class TokenContextAuditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("admin");
    }
}
