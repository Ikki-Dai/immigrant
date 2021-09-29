package com.ikki.immigrant.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class AuditAwareTest {

    @Test
    void test() {
        AuditAware auditAware = new AuditAware();
        Optional<String> auditor = auditAware.getCurrentAuditor();
        Assertions.assertTrue(!auditor.isPresent());
    }
}
