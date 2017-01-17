package se.kumliens.concept2runkeeper.concept2;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Created by svante2 on 2017-01-16.
 */
@Component
@ConfigurationProperties(prefix = "concept2", locations = "file:/data/c2r/config/application.properties", ignoreUnknownFields = false)
@Data
@ToString
public class Concept2Props {

    @Value("${concept2.oauth2-client-id}") //When deploying on pws there is no /data directory
    private String oauth2ClientId;

    @Value("${concept2.oauth2-client-secret}")
    private String oauth2ClientSecret;
    private URI userResource;
    private URI profileResource;
    private URI fitnessActivityResource;
    private URI baseUrl;
}
