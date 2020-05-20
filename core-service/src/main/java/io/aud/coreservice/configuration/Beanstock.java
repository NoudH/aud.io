package io.aud.coreservice.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beanstock {

    @Bean
    public Queue newUserQueue() {
        return new Queue("CoreService_NewUser", true);
    }

    @Bean
    public Queue lockoutAccount() { return new Queue("EmailService_LockoutAccount", true); }

    @Bean
    public Queue forgotPassword() { return new Queue("EmailService_ForgotPassword", true); }

    @Bean
    public Queue changedPassword() { return new Queue("EmailService_ChangedPassword", true); }

    @Bean
    public Queue deleteAccount() { return new Queue("EmailService_DeleteAccount", true); }

    @Bean
    public Queue activateAccount() {return new Queue("EmailService_ActivateAccount", true);}
}
