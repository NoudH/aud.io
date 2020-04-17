package io.aud.authenticationservice.repositories;

import io.aud.authenticationservice.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsernameOrEmail(String username, String email);
    Optional<Account> findByEmail(String email);
}
