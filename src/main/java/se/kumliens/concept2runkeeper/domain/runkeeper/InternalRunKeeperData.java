package se.kumliens.concept2runkeeper.domain.runkeeper;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * Collection of data related to RunKeeper which comes from our own system.
 *
 * Created by svante2 on 2016-12-08.
 */
@Builder(builderClassName = "Builder")
@Getter
@ToString
public class InternalRunKeeperData {

    private String token;

    private Instant firstConnected;

    private Instant lastTimeConnected;

    private String defaultComment;

    private Boolean postToTwitterOverride;

    private Boolean postToFacebookOverride;

    public static Builder from(InternalRunKeeperData runKeeperData) {
        return builder()
                .firstConnected(runKeeperData.firstConnected)
                .lastTimeConnected(runKeeperData.lastTimeConnected)
                .token(runKeeperData.token)
                .defaultComment(runKeeperData.getDefaultComment());
    }

    public void setDefaultComment(String defaultComment) {
        this.defaultComment = defaultComment;
    }

    public void setPostToTwitterOverride(boolean postToTwitterOverride) {
        this.postToTwitterOverride = postToTwitterOverride;
    }

    public void setPostToFacebookOverride(boolean postToFacebookOverride) {
        this.postToFacebookOverride = postToFacebookOverride;
    }

    public void setLastTimeConnected(Instant lastTimeConnected) {
        this.lastTimeConnected = lastTimeConnected;
    }
}
