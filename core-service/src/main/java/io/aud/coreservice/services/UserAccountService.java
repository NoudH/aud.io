package io.aud.coreservice.services;

import io.aud.coreservice.domain.UserAccount;
import io.aud.coreservice.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    private UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount newUserRegistered(UserAccount account) {
        return userAccountRepository.save(account);
    }

    public UserAccount findByEmail(String email) {
        return userAccountRepository.findByEmail(email).get();
    }
}
