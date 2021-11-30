package com.ikki.immigrant.domain.subject;

import com.ikki.immigrant.domain.subject.entity.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Long> {

    Optional<Subject> findByPhone(String phone);

    Optional<Subject> findByEmail(String email);

    Optional<Subject> findByUsername(String username);
}
