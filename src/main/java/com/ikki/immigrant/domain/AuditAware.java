package com.ikki.immigrant.domain;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditAware implements AuditorAware {
    @Override
    public Optional getCurrentAuditor() {
        return Optional.empty();
    }
}
