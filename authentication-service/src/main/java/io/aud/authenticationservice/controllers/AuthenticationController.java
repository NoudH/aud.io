package io.aud.authenticationservice.controllers;

import io.aud.authenticationservice.domain.Account;
import io.aud.authenticationservice.domain.AccountStatus;
import io.aud.authenticationservice.services.AccountService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountLockedException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/")
public class AuthenticationController {

    private AccountService accountService;

    public AuthenticationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("login")
    public ResponseEntity<String> Login(Account account) {
        try {
            return ResponseEntity.ok(accountService.login(account));
        } catch (AccountLockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{status: \"failed\", reason: \"lockout\"}");
        } catch (AuthorizationServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{status: \"failed\", reason: \"auth failed\"}");
        }
    }

    @PostMapping("sign-up")
    public Account signUp(Account account){
        return accountService.signUp(account);
    }

}
