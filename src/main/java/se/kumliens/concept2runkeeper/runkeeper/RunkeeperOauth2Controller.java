package se.kumliens.concept2runkeeper.runkeeper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by svante2 on 2016-11-28.
 */
@RestController
@RequestMapping("runkeeper")
@Slf4j
@RequiredArgsConstructor
public class RunkeeperOauth2Controller {

    private final RunkeeperService runkeeperService;


    @GetMapping(value = "auth_response")
    public void handleAuthResponse(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "error", required = false) String error, @RequestParam(value = "state", required = false) String state) {
        log.info("Got a code: {}, state: {} and error: {}", code, state, error);
        if(StringUtils.hasText(code)) {
            String accessToken = runkeeperService.askForToken(code);
        }
    }
}
