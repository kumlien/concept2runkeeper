package se.kumliens.concept2runkeeper.runkeeper;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.api.client.util.Lists;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by svante2 on 2016-11-28.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RunkeeperService {

    private final RunkeeperProps props;

    private RestTemplate restTemplate;

    HttpHeaders headersForTokenRequest = new HttpHeaders();

    @PostConstruct
    public void setup() {
        headersForTokenRequest.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headersForTokenRequest.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        restTemplate = new RestTemplate();
    }


    public String askForToken(String code) {
                RunkeeperTokenRequest request = RunkeeperTokenRequest.builder().code(code).client_id(props.getOauth2ClientId())
                .client_secret(props.getOauth2ClientSecret()).redirect_uri("oops...").build();

        HttpEntity<RunkeeperTokenRequest> body = new HttpEntity(request, headersForTokenRequest);
        ResponseEntity<RunkeeperTokenResponse> responseEntity = restTemplate.postForEntity(props.getOauth2UrlToken(), body, RunkeeperTokenResponse.class);

        return responseEntity.getBody().getAccess_token();
    }

    @Builder
    @Getter
    public static class RunkeeperTokenRequest {
        private final String grant_type = "authorization_code";
        private String code;
        private String client_id;
        private String client_secret;
        private String redirect_uri;
    }

    @Data
    public static class RunkeeperTokenResponse {
        private String access_token;
    }
}
