package com.ikki.immigrant.domain.credentials;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
    Optional<Credentials> findByUid(Long uid);

}
