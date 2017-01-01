package se.kumliens.concept2runkeeper.runkeeper;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;

/**
 * Created by svante2 on 2016-11-28.
 */
@Component
@ConfigurationProperties(prefix = "runkeeper", locations = "file:/data/c2r/config/application.properties", ignoreUnknownFields = false)
@Data
@ToString
public class RunkeeperProps {

    @Value("${runkeeper.oauth2-client-id}")
    private String oauth2ClientId;

    @Value("${runkeeper.oauth2-client-secret}")
    private String oauth2ClientSecret;
    private URI userResource;
    private URI profileResource;
    private URI fitnessActivityResource;
    private URI baseUrl;


}
