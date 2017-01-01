package se.kumliens.concept2runkeeper.configuration;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

/**
 * Provide a MailSender when running in CF (the SendGrid service)
 *
 * Created by svante2 on 2017-01-01.
 */
@Profile("cloud")
@Configuration
@ServiceScan
public class MailCloudConfiguration extends AbstractCloudConfig {

    @Bean
    public MailSender mailSender() {
        return connectionFactory().service(MailSender.class);
    }
}
