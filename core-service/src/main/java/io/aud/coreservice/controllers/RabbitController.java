package io.aud.coreservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aud.coreservice.domain.UserAccount;
import io.aud.coreservice.services.UserAccountService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitController {

    private UserAccountService userAccountService;

    public RabbitController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @RabbitListener(queues = {"CoreService_NewUser"})
    public void newUserRegistered(String message) throws JsonProcessingException {
        UserAccount account = new ObjectMapper().readValue(message, UserAccount.class);
        userAccountService.newUserRegistered(account);
    }
}
