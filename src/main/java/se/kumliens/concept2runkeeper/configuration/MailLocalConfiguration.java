package se.kumliens.concept2runkeeper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Created by svante2 on 2017-01-01.
 */
@Profile("!cloud")
@Configuration
public class MailLocalConfiguration {

    @Bean
    public MailSender mailSender(MailPropsLocal mailProps) {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailProps.getHost());
        mailSenderImpl.setPort(587);
        mailSenderImpl.setUsername(mailProps.getUser());
        mailSenderImpl.setPassword(mailProps.getPassword());

        return mailSenderImpl;
    }
}
