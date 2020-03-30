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
}
