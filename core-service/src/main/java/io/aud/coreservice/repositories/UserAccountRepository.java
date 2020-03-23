package io.aud.coreservice.repositories;

import io.aud.coreservice.domain.UserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserAccountRepository extends PagingAndSortingRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);
}
