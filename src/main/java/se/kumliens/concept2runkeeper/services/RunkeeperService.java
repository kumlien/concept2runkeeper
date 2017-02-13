package se.kumliens.concept2runkeeper.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rjeschke.txtmark.Run;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import se.kumliens.concept2runkeeper.domain.runkeeper.ExternalRunkeeperData;
import se.kumliens.concept2runkeeper.domain.runkeeper.InternalRunKeeperData;
import se.kumliens.concept2runkeeper.domain.runkeeper.RunKeeperProfile;
import se.kumliens.concept2runkeeper.domain.runkeeper.RunKeeperUser;
import se.kumliens.concept2runkeeper.domain.runkeeper.RunkeeperActivity;
import se.kumliens.concept2runkeeper.runkeeper.RecordActivityRequest;
import se.kumliens.concept2runkeeper.runkeeper.RunKeeperSettings;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperProps;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.kumliens.concept2runkeeper.runkeeper.UpdateActivityRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

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
        log.debug("Got some props: {}", props);
        restTemplate = new RestTemplate();

        // set up a buffering request factory, so response body is always buffered
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(requestFactory);
        requestFactory.setOutputStreaming(false);
        restTemplate.setRequestFactory(bufferingClientHttpRequestFactory);

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return super.hasError(clientHttpResponse);
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                InputStream is = clientHttpResponse.getBody();
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String body = s.hasNext() ? s.next() : "";
                log.info("{}", body);
                int statusCode = clientHttpResponse.getStatusCode().value();
                if(statusCode == 404) {
                    throw new NoSuchActivityException();
                }
                super.handleError(clientHttpResponse);
            }
        });
    }

    public void disconnectUser(String accessToken) {
        //TODO
    }


    /**
     * Get all user data from RunKeeper in one go.
     *
     * @param token
     * @return A RunKeeperDataBuilder to indicate that it's not a complete {@link InternalRunKeeperData} object
     */
    public ExternalRunkeeperData.Builder getUserData(String token) {
        RunKeeperUser user = getUser(token);
        RunKeeperProfile profile = getProfile(token);
        RunKeeperSettings settings = getSettings(user, token);
        return ExternalRunkeeperData.builder().user(user).profile(profile).settings(settings);
    }

    public RunKeeperSettings getSettings(RunKeeperUser user,  String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.com.runkeeper.Settings+json");
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, GET, URI.create(props.getBaseUrl().toString().concat(user.getSettingsResource())));
        ResponseEntity<RunKeeperSettings> response = restTemplate.exchange(requestEntity, RunKeeperSettings.class);
        log.info("got some settings: {}", response.getBody());
        return response.getBody();
    }

    public RunKeeperUser getUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.com.runkeeper.User+json");
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, GET, props.getUserResource());
        ResponseEntity<RunKeeperUser> response = restTemplate.exchange(requestEntity, RunKeeperUser.class);
        log.info("got a user: {}", response.getBody());
        return response.getBody();
    }

    public RunKeeperProfile getProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.com.runkeeper.Profile+json");
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, GET, props.getProfileResource());
        ResponseEntity<RunKeeperProfile> response = restTemplate.exchange(requestEntity, RunKeeperProfile.class);
        log.info("got a profile: {}", response.getBody());
        return response.getBody();
    }


    public URI recordActivity(RecordActivityRequest request, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/vnd.com.runkeeper.NewFitnessActivity+json");
        headers.set("Authorization", "Bearer " + token);
        log.info("Sending a {}", request);
        RequestEntity requestEntity = new RequestEntity(request, headers, POST, props.getFitnessActivityResource());
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        return response.getHeaders().getLocation();
    }

    public RunkeeperActivity getActivity(String token, URI activityLocation) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.com.runkeeper.FitnessActivity+json");
        headers.set("Authorization", "Bearer " + token);
        RequestEntity requestEntity = new RequestEntity(headers, GET, URI.create(props.getBaseUrl().toString().concat(activityLocation.toString())));
        try {
            log.info("{}",objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestEntity));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ResponseEntity<RunkeeperActivity> response = restTemplate.exchange(requestEntity, RunkeeperActivity.class);
        log.info("got an activity: {}", response.getBody());
        return response.getBody();
    }

    public RunkeeperActivity updateActivity(RunkeeperActivity runkeeperActivity, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.com.runkeeper.FitnessActivity+json");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/vnd.com.runkeeper.FitnessActivity+json");
        UpdateActivityRequest request = null;
        try {
            request = UpdateActivityRequest.from(runkeeperActivity);
        } catch (ParseException e) {
            log.warn("Failed to create a UpdateActivityRequest from a {}", runkeeperActivity, e);
            throw new RuntimeException(e);
        }
        RequestEntity requestEntity = new RequestEntity(request, headers, PUT, URI.create(props.getBaseUrl().toString().concat(runkeeperActivity.getUri())));

        ResponseEntity<RunkeeperActivity> response = restTemplate.exchange(requestEntity, RunkeeperActivity.class);
        log.info("got an updated activity: {}", response.getBody());
        return response.getBody();
    }
}
