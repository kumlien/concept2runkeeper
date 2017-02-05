package se.kumliens.concept2runkeeper.concept2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_ROOT_VALUE;
import static org.springframework.http.HttpMethod.GET;

/**
 * Created by svante2 on 2017-02-05.
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

}
