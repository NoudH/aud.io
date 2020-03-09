package io.aud.authenticationservice.configuration;

import io.aud.authenticationservice.domain.Account;
import io.aud.authenticationservice.repositories.AccountRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {
    private AccountRepository accountRepository;

    public UserDetailsServiceImplementation(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findByUsernameOrEmail(username, username);

        if(!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException(username);
        }

        Account account = optionalAccount.get();
        return new User(account.getUsername(), account.getPassword(), AuthorityUtils.createAuthorityList(account.getClaims().toArray(new String[0])));
    }
}
