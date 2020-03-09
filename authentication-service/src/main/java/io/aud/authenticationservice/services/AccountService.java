package io.aud.authenticationservice.services;

import io.aud.authenticationservice.domain.Account;
import io.aud.authenticationservice.domain.AccountStatus;
import io.aud.authenticationservice.repositories.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.security.auth.login.AccountLockedException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AccountService {

    @Value("${security.account-claims.users}")
    private List<String> CLAIMS;

    @Value("$(security.lockout.threshold)")
    private Integer LOCKOUT_THRESHOLD;

    @Value("${security.jwt.token.issuer}")
    private String ISSUER;

    @Value("${security.jwt.token.secret-key}")
    private String SECRET;

    @Value("${security.jwt.token.expire-length}")
    private long EXPIRATION; //in ms

    private AccountRepository accountRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountService(AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String login(Account account) throws AccountLockedException {
        Account accountEntity = accountRepository.findByUsernameOrEmail(account.getUsername(), account.getEmail()).get();
        if(accountEntity.getStatus().equals(AccountStatus.ACTIVATED) && bCryptPasswordEncoder.matches(account.getPassword(), accountEntity.getPassword())){
            return generateToken(EXPIRATION, accountEntity.getEmail(), accountEntity.getClaims());
        }
        else {
            accountEntity.incrementLockoutCounter();
            accountRepository.save(accountEntity);
            if(accountEntity.getLockoutCounter() >= LOCKOUT_THRESHOLD){ throw new AccountLockedException("Account Locked Out!"); }
            throw new AuthorizationServiceException("Login failed");
        }
    }

    public Account signUp(Account account){
        account.setClaims(CLAIMS);
        account.setStatus(AccountStatus.AWAITING_CONFIRMATION);
        return accountRepository.save(account);
    }

    private String generateToken(Long expiration, String subject, List<String> authorities){
        Claims claims = Jwts.claims()
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration))
                .setSubject(subject);

        //Create token
        String token = Jwts.builder()
                .addClaims(claims)
                .claim("authorities", authorities)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        return token;
    }
}
