package io.aud.authenticationservice.repositories;

import io.aud.authenticationservice.domain.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByUsernameOrEmail(String username, String email);
    Optional<Account> findByEmail(String email);
}
