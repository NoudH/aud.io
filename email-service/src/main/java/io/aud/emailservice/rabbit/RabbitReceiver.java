package io.aud.emailservice.rabbit;

import com.planetexpress.jwtsecurity.utils.JwtUtil;
import io.aud.emailservice.email.EmailController;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class RabbitReceiver {

    private EmailController emailController;
    private JwtUtil jwtUtil;

    public RabbitReceiver(EmailController emailController, JwtUtil jwtUtil) {
        this.emailController = emailController;
        this.jwtUtil = jwtUtil;
    }

    @RabbitListener(queues = {"EmailService_ActivateAccount"})
    public void sendConfirmAccountEmail(String message) throws IOException, MessagingException {
        if(jwtUtil.validateToken(message)) {
            String email = jwtUtil.parseToken(message).getBody().getSubject();
            if (email != null) {
                emailController.sendAccountConfirmationEmail(email, message);
            }
        }
    }

    @RabbitListener(queues = {"EmailService_ForgotPassword"})
    public void sendResetPasswordEmail(String message) throws IOException, MessagingException {
        if(jwtUtil.validateToken(message)) {
            String email = jwtUtil.parseToken(message).getBody().getSubject();
            if (email != null) {
                emailController.sendResetPasswordEmail(email, message);
            }
        }
    }

    @RabbitListener(queues = {"EmailService_DeleteAccount"})
    public void sendDeleteAccountEmail(String message) throws IOException, MessagingException {
        if(jwtUtil.validateToken(message)) {
            String email = jwtUtil.parseToken(message).getBody().getSubject();
            if (email != null) {
                emailController.sendDeleteAccountEmail(email, message);
            }
        }
    }

    @RabbitListener(queues = {"EmailService_ChangedPassword"})
    public void sendPasswordChangedEmail(String message) throws IOException, MessagingException {
        if(jwtUtil.validateToken(message)) {
            String email = jwtUtil.parseToken(message).getBody().getSubject();
            if (email != null) {
                emailController.sendPasswordChangedEmail(email);
            }
        }
    }

    @RabbitListener(queues = {"EmailService_LockoutAccount"})
    public void sendAccountLockedOutEmail(String message) throws IOException, MessagingException {
        if(jwtUtil.validateToken(message)) {
            String email = jwtUtil.parseToken(message).getBody().getSubject();
            if (email != null) {
                emailController.sendAccountLockedOutEmail(email, message);
            }
        }
    }
}
