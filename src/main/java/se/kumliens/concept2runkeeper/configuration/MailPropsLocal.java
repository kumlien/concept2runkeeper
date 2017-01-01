package se.kumliens.concept2runkeeper.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by svante2 on 2017-01-01.
 */
@Component
@ConfigurationProperties(prefix = "smtp", locations = "file:/data/c2r/config/application.properties", ignoreUnknownFields = false)
@Profile("!cloud")
@Data
@ToString
public class MailPropsLocal {

    private String host;

    private String user;

    private String password;
}