package io.aud.authenticationservice.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanStock {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Queue activateAccountQueue() {
        return new Queue("EmailService_ActivateAccount", true);
    }

    @Bean
    public Queue forgotPasswordQueue() {
        return new Queue("EmailService_ForgotPassword", true);
    }

    @Bean
    public Queue passwordChangedQueue() {
        return new Queue("EmailService_ChangedPassword", true);
    }

    @Bean
    public Queue deleteAccountQueue() {
        return new Queue("EmailService_DeleteAccount", true);
    }

    @Bean
    public Queue lockoutAccountQueue() {
        return new Queue("EmailService_LockoutAccount", true);
    }
}
