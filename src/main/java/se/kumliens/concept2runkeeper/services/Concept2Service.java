package se.kumliens.concept2runkeeper.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.kumliens.concept2runkeeper.concept2.Concept2Props;
import se.kumliens.concept2runkeeper.domain.concept2.Concept2ApiActivity;
import se.kumliens.concept2runkeeper.domain.concept2.Concept2User;
import se.kumliens.concept2runkeeper.domain.runkeeper.RunKeeperUser;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.annotation.PostConstruct;

import java.net.URI;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.http.HttpMethod.GET;

/**
 * Internal api to concept2
 *
 * Created by svante on 2017-01-26.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Concept2Service {

    private final Concept2Props props;

    private RestTemplate restTemplate;


    @PostConstruct
    public void setUp() {
        restTemplate = new RestTemplate();
    }


    public Concept2User getUserData(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(newArrayList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, GET, props.getUserResource());
        ResponseEntity<Concept2User> response = restTemplate.exchange(requestEntity, Concept2User.class);
        log.info("got a user: {}", response.getBody());
        return response.getBody();
    }

    public List<Concept2ApiActivity> getActivities(String token) {
        return null;
    }

}
