package se.kumliens.concept2runkeeper.runkeeper;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URL;
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

    public void disconnectUser(String accessToken) {
        //TODO
    }

    public RunKeeperUser getUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.com.runkeeper.User+json");
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET, props.getUserResource());
        ResponseEntity<RunKeeperUser> response = restTemplate.exchange(requestEntity, RunKeeperUser.class);
        log.info("got a user: {}", response.getBody());
        return response.getBody();
    }

    public RunKeeperProfile getProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.com.runkeeper.Profile+json");
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET, props.getProfileResource());
        ResponseEntity<RunKeeperProfile> response = restTemplate.exchange(requestEntity, RunKeeperProfile.class);
        log.info("got a profile: {}", response.getBody());
        return response.getBody();
    }


}
