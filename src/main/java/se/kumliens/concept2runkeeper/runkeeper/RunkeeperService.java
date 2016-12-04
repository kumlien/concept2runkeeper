package se.kumliens.concept2runkeeper.runkeeper;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;

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
        MultiValueMap<String, String> request = RunkeeperTokenRequest.builder().code(code).client_id(props.getOauth2ClientId())
                .client_secret(props.getOauth2ClientSecret()).redirect_uri(props.getOauth2CallbackUrl().toString()).build().toMap();

        log.info("Sending request to {} accessToken: {}", props.getOauth2UrlToken(), request);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request, headersForTokenRequest);
        ResponseEntity<RunkeeperTokenResponse> responseEntity = restTemplate.postForEntity(props.getOauth2UrlToken(), requestEntity, RunkeeperTokenResponse.class);
        log.info("Got a response back: {}", responseEntity);

        return responseEntity.getBody().getAccess_token();
    }

    public void disconnectUser(String accessToken) {
        //TODO
    }

    @Builder
    @ToString
    public static class RunkeeperTokenRequest {
        private final String grant_type = "authorization_code";
        private String code;
        private String client_id;
        private String client_secret;
        private String redirect_uri;

        MultiValueMap<String, String> toMap() {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", grant_type);
            map.add("code", code);
            map.add("client_id", client_id);
            map.add("client_secret", client_secret);
            map.add("redirect_uri", redirect_uri);
            return map;
        }
    }

    @Data
    public static class RunkeeperTokenResponse {
        private String access_token;
    }
}
