package se.kumliens.concept2runkeeper.runkeeper;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * Created by svante2 on 2016-11-28.
 */
@Component
@ConfigurationProperties(prefix = "runkeeper")
@Data
@ToString
public class RunkeeperProps {

    private String oauth2ClientId;
    private String oauth2ClientSecret;
    private URL oauth2UrlAuthorize;
    private URL oauth2UrlToken;

}
