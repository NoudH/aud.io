package io.aud.emailservice.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class EmailController {

    @Value("${emails.application.host}")
    private String APPLICATION_HOST;

    @Value("${emails.application.activate-account.endpoint}")
    private String ENDPOINT_CONFIRM_ACCOUNT;

    @Value("${emails.application.reset-password.endpoint}")
    private String ENDPOINT_RESET_PASSWORD;

    @Value("${emails.application.delete-account.endpoint}")
    private String ENDPOINT_DELETE_ACCOUNT;

    @Value("classpath:email-templates/${emails.templates.activate-account}")
    private Resource EMAIL_CONFIRM_ACCOUNT;

    @Value("classpath:email-templates/${emails.templates.reset-password}")
    private Resource EMAIL_RESET_PASSWORD;

    @Value("classpath:email-templates/${emails.templates.delete-account}")
    private Resource EMAIL_DELETE_ACCOUNT;

    @Value("classpath:email-templates/${emails.templates.password-changed}")
    private Resource EMAIL_PASSWORD_CHANGED;

    @Value("classpath:email-templates/${emails.templates.account-locked-out}")
    private Resource EMAIL_ACCOUNT_LOCKED_OUT;

    private JavaMailSender javaMailSender;

    public EmailController(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendAccountConfirmationEmail(String email, String token) throws IOException, MessagingException {
        String text = new TemplateBuilder(resourceContentToString(EMAIL_CONFIRM_ACCOUNT))
            .replace("host", APPLICATION_HOST)
            .replace("endpoint", ENDPOINT_CONFIRM_ACCOUNT)
            .replace("token", token)
            .toString();

        sendEmail(email, "Confirm Account", text);
    }

    public void sendResetPasswordEmail(String email, String token) throws IOException, MessagingException {
        String text = new TemplateBuilder(resourceContentToString(EMAIL_RESET_PASSWORD))
            .replace("host", APPLICATION_HOST)
            .replace("endpoint", ENDPOINT_RESET_PASSWORD)
            .replace("token", token)
            .toString();

        sendEmail(email, "Reset password", text);
    }

    public void sendDeleteAccountEmail(String email, String token) throws IOException, MessagingException {
        String text = new TemplateBuilder(resourceContentToString(EMAIL_DELETE_ACCOUNT))
                .replace("host", APPLICATION_HOST)
                .replace("endpoint", ENDPOINT_DELETE_ACCOUNT)
                .replace("token", token)
                .toString();

        sendEmail(email, "Account deletion", text);
    }

    public void sendPasswordChangedEmail(String email) throws IOException, MessagingException {
        String text = new TemplateBuilder(resourceContentToString(EMAIL_PASSWORD_CHANGED)).toString();

        sendEmail(email, "Your password has been changed", text);
    }

    public void sendAccountLockedOutEmail(String email, String token) throws IOException, MessagingException {
        String text = new TemplateBuilder(resourceContentToString(EMAIL_ACCOUNT_LOCKED_OUT))
                .replace("host", APPLICATION_HOST)
                .replace("endpoint", ENDPOINT_CONFIRM_ACCOUNT)
                .replace("token", token)
                .toString();

        sendEmail(email, "Your account has been locked out!", text);
    }

    private void sendEmail(String email, String subject, String text) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(text, true);

        javaMailSender.send(mail);
    }

    private String resourceContentToString(Resource resource) throws IOException {
        InputStream stream = resource.getInputStream();
        return FileCopyUtils.copyToString(new InputStreamReader(stream, UTF_8));
    }
}
