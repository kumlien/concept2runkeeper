package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
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

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void setup() {
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


    public URI recordActivity(RecordActivityRequest request, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/vnd.com.runkeeper.NewFitnessActivity+json");
        headers.set("Authorization", "Bearer " + token);
        log.info("Sending a {}", request);

        try {
            log.info("{}",objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        RequestEntity requestEntity = new RequestEntity(request, headers, HttpMethod.POST, props.getFitnessActivityResource());
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        return response.getHeaders().getLocation();
    }
}
