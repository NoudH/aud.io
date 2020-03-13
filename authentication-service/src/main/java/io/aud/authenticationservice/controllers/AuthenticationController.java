package io.aud.authenticationservice.controllers;

import io.aud.authenticationservice.domain.Account;
import io.aud.authenticationservice.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.security.auth.login.AccountLockedException;

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

    @PreAuthorize("hasAnyAuthority('ACTIVATE_ACCOUNT')")
    @PutMapping("activate")
    public void activateAccount(@ApiIgnore Authentication authentication) {
        accountService.activate(authentication);
    }

    @GetMapping("reset-password")
    public void requestPasswordReset(String email){
        accountService.requestPasswordReset(email);
    }

    @PreAuthorize("hasAnyAuthority('RESET_PASSWORD')")
    @PutMapping("reset-password")
    public void changePassword(@ApiIgnore Authentication authentication, String newPassword){
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
