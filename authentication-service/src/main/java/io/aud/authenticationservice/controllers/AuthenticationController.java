package io.aud.authenticationservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.aud.authenticationservice.domain.Account;
import io.aud.authenticationservice.services.AccountService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.security.auth.login.AccountLockedException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/")
public class AuthenticationController {

    private AccountService accountService;

    public AuthenticationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("login")
    public ResponseEntity<String> Login(@RequestBody Account account) {
        try {
            return ResponseEntity.ok(accountService.login(account));
        } catch (AccountLockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (AuthorizationServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed!");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("This account does not exist!");
        }
    }

    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@RequestBody Account account) {
        try{
            return ResponseEntity.ok(accountService.signUp(account));
        } catch (ConstraintViolationException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{status: \"failed\", reason: \"Email and/or username has already been taken!\"}");
        }
    }

    @PreAuthorize("hasAnyAuthority('ACTIVATE_ACCOUNT')")
    @PutMapping("activate")
    public ResponseEntity<?> activateAccount(@ApiIgnore Authentication authentication) throws JsonProcessingException {
        try{
            accountService.activate(authentication);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("reset-password")
    public void requestPasswordReset(@RequestBody String email){
        accountService.requestPasswordReset(email);
    }

    @PreAuthorize("hasAnyAuthority('RESET_PASSWORD')")
    @PutMapping("reset-password")
    public void changePassword(@ApiIgnore Authentication authentication, @RequestBody String newPassword){
        accountService.changePassword(authentication, newPassword);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("delete")
    public void requestAccountDeletion(@ApiIgnore Authentication authentication) {
        accountService.requestAccountDeletion(authentication);
    }

    @PreAuthorize("hasAnyAuthority('ACCOUNT_DELETE_SELF')")
    @DeleteMapping("delete")
    public void deleteAccount(@ApiIgnore Authentication authentication) {
        accountService.deleteAccount(authentication);
    }
}
