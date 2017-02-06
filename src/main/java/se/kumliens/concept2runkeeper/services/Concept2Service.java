package se.kumliens.concept2runkeeper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_ROOT_VALUE;
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

    private RestTemplate restTemplate; //TODO inject

    @PostConstruct
    public void setUp() {
        restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(UNWRAP_ROOT_VALUE, true);
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(objectMapper);
        messageConverters.add(jsonMessageConverter);
        restTemplate.setMessageConverters(messageConverters);
    }

    public Concept2User getUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, GET, props.getUserResource());
        ResponseEntity<Concept2User> response = restTemplate.exchange(requestEntity, Concept2User.class);
        log.debug("got a user: {}", response.getBody());
        return response.getBody();
    }

    public List<Concept2ApiActivity> getActivities(String token) {
        return null;
    }

}
