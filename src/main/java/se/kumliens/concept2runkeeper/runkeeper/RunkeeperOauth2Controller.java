package se.kumliens.concept2runkeeper.runkeeper;

import com.vaadin.spring.annotation.SpringView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.support.ApplicationContextEventBroker;
import se.kumliens.concept2runkeeper.vaadin.events.RunkeeperAuthArrivedEvent;

/**
 * Created by svante2 on 2016-11-28.
 */
@RestController
@RequestMapping("runkeeper")
@Slf4j
@RequiredArgsConstructor
public class RunkeeperOauth2Controller {

    private final ApplicationContextEventBroker eventBroker;

    private final RunkeeperService runkeeperService;

    @GetMapping(value = "auth_response")
    public ResponseEntity<String> handleAuthResponse(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "error", required = false) String error, @RequestParam(value = "state", required = false) String userId) {
        log.info("Got a code: {}, state: {} and error: {}", code, userId, error);
        if(StringUtils.hasText(code)) {
            String accessToken = runkeeperService.askForToken(code);
            log.info("Got an access accessToken back: {}", accessToken);
            eventBroker.onApplicationEvent(new RunkeeperAuthArrivedEvent(this, userId, accessToken));
            return new ResponseEntity<>("<b>Success!</b></br>You can now close this pop-up", HttpStatus.OK);
        } else if (StringUtils.hasText(error)){
            log.info("Unable to get a accessToken since the user denied us access");
            return new ResponseEntity<String>("<b>No success...</b></br>In order to make this work you need to give us permission to add activities to your RunKeeper account", HttpStatus.OK);
        } else  {
            log.warn("Unable to get a accessToken for some unknown reason...");
            return new ResponseEntity<String>("<b>No success...</b></br>We experienced a problem fetching your accessToken, please try again...", HttpStatus.OK);
        }
    }
}
