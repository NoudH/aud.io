package io.aud.authenticationservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aud.authenticationservice.domain.Account;
import io.aud.authenticationservice.domain.AccountStatus;
import io.aud.authenticationservice.repositories.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountLockedException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class AccountService {

    @Value("${security.account-claims.users}")
    private List<String> CLAIMS;

    @Value("${security.lockout.threshold}")
    private int LOCKOUT_THRESHOLD;

    @Value("${security.jwt.token.issuer}")
    private String ISSUER;

    @Value("${security.jwt.token.secret-key}")
    private String SECRET;

    @Value("${security.jwt.token.expire-length}")
    private long EXPIRATION; //in ms

    @Value("${security.jwt.token.reset-password-expire-length}")
    private long PASSWORD_RESET_EXPIRATION; //in ms

    @Value("${security.jwt.token.activate-account-expire-length}")
    private long ACTIVATE_ACCOUNT_EXPIRATION; //in ms

    @Value("${security.jwt.token.delete-account-expire-length}")
    private long DELETE_ACCOUNT_EXPIRATION; //in ms

    private AccountRepository accountRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RabbitTemplate rabbitTemplate;

    public AccountService(AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RabbitTemplate rabbitTemplate) {
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    public String login(Account account) throws AccountLockedException, AuthorizationServiceException, NoSuchElementException {
        Account accountEntity = accountRepository.findByUsernameOrEmail(account.getUsername(), account.getEmail()).get();
        if(accountEntity.getStatus().equals(AccountStatus.ACTIVATED) && bCryptPasswordEncoder.matches(account.getPassword(), accountEntity.getPassword())){
            accountEntity.setLockoutCounter(0);
            accountRepository.save(accountEntity);
            return generateToken(EXPIRATION, accountEntity.getEmail(), accountEntity.getClaims());
        }
        else if (accountEntity.getStatus().equals(AccountStatus.AWAITING_CONFIRMATION)) {
            throw new AccountLockedException("This account is awaiting confirmation! Please check your email!");
        }
        else {
            accountEntity.incrementLockoutCounter();
            accountRepository.save(accountEntity);
            if(accountEntity.getLockoutCounter() >= LOCKOUT_THRESHOLD - 1) {
                rabbitTemplate.convertAndSend(
                        "EmailService_LockoutAccount",
                        generateToken(ACTIVATE_ACCOUNT_EXPIRATION, accountEntity.getEmail(), Collections.singletonList("ACTIVATE_ACCOUNT"))
                );
                accountEntity.setStatus(AccountStatus.LOCKED_OUT);
                accountRepository.save(accountEntity);
                throw new AccountLockedException("Account has been locked out! Please check your email!");
            }
            throw new AuthorizationServiceException("Login failed");
        }
    }

    public Account signUp(Account account){
        account.setClaims(CLAIMS);
        account.setStatus(AccountStatus.AWAITING_CONFIRMATION);
        account.setLockoutCounter(0);
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        Account returnEntity = accountRepository.save(account);

        rabbitTemplate.convertAndSend(
                "EmailService_ActivateAccount",
                generateToken(ACTIVATE_ACCOUNT_EXPIRATION, returnEntity.getEmail(), Collections.singletonList("ACTIVATE_ACCOUNT"))
        );

        return returnEntity;
    }

    public void activate(Authentication authentication) throws JsonProcessingException, IllegalArgumentException {
        Account account = accountRepository.findByEmail(authentication.getName()).get();
        if(account.getStatus().equals(AccountStatus.ACTIVATED)){
            throw new IllegalArgumentException("This account has already been activated!");
        }
        account.setStatus(AccountStatus.ACTIVATED);
        account = accountRepository.save(account);

        rabbitTemplate.convertAndSend("CoreService_NewUser", new ObjectMapper().writeValueAsString(account));
    }

    public void requestPasswordReset(String email) {
        Account account = accountRepository.findByEmail(email).get();

        rabbitTemplate.convertAndSend(
                "EmailService_ForgotPassword",
                generateToken(PASSWORD_RESET_EXPIRATION, account.getEmail(), Collections.singletonList("RESET_PASSWORD"))
        );
    }

    public void changePassword(Authentication authentication, String newPassword) {
        Account account = accountRepository.findByEmail(authentication.getName()).get();
        account.setPassword(bCryptPasswordEncoder.encode(newPassword));
        accountRepository.save(account);

        rabbitTemplate.convertAndSend("EmailService_ChangedPassword", account.getEmail());
    }

    public void requestAccountDeletion(Authentication authentication) {
        Account account = accountRepository.findByEmail(authentication.getName()).get();

        rabbitTemplate.convertAndSend(
                "EmailService_DeleteAccount",
                generateToken(DELETE_ACCOUNT_EXPIRATION, account.getEmail(), Collections.singletonList("ACCOUNT_DELETE_SELF"))
        );
    }

    public void deleteAccount(Authentication authentication) {
        Account account = accountRepository.findByEmail(authentication.getName()).get();
        accountRepository.delete(account);
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
