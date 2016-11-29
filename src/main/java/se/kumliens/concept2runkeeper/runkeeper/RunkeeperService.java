package se.kumliens.concept2runkeeper.runkeeper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;

/**
 * Created by svante2 on 2016-11-28.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RunkeeperService {

    private final RunkeeperProps props;

    @PostConstruct
    public void init() {
        log.info("hej: {}", props);
    }

    public void startAuth() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(props.getOauth2UrlAuthorize().toURI(), String.class);
    }
}
