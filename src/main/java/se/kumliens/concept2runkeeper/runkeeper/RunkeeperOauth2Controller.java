package se.kumliens.concept2runkeeper.runkeeper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

/**
 * Created by svante2 on 2016-11-28.
 */
@RestController
@RequestMapping("runkeeper")
@Slf4j
public class RunkeeperOauth2Controller {


    @GetMapping(value = "auth_response")
    public String handleAuthResponse(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "error", required = false) String error, @RequestParam(value = "state", required = false) String state) {
        log.info("Got a code: {}, state: {} and error: {}", code, state, error);
        if(StringUtils.hasText(code)) {
            return "Excellent, you can now close this window";
        }
        return "Sorry to hear that...";
    }
}
