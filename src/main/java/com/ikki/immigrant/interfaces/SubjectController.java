package com.ikki.immigrant.interfaces;

import com.ikki.immigrant.infrastructure.exception.BizException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    /**
     * check exist
     *
     * @param subject
     * @return
     */
    @GetMapping("/{subject}")
    public ResponseEntity<String> checkOnly(@PathVariable("subject") String subject) {
        if ("test".equals(subject)) {
            throw BizException.of(HttpStatus.CONFLICT, "test_args", new Object[]{"tests"});
        }

        return ResponseEntity.ok(subject);
    }
}
