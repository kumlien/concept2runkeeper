package se.kumliens.concept2runkeeper.runkeeper;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Collection of data from RunKeeper resources
 *
 * Created by svante2 on 2016-12-08.
 */
@Builder
@Getter
public class RunKeeperData {

    private RunKeeperUser user;

    private RunKeeperProfile profile;

    private String token;

    private Instant firstConnected;

    private Instant lastTimeConnected;
}
