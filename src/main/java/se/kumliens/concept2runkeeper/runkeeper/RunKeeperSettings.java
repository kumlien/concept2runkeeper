package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by svante2 on 2016-12-15.
 */
@Getter
@ToString
public class RunKeeperSettings {

    private final boolean facebookConnected;

    private final boolean twitterConnected;

    private final boolean postToFacebook;

    private final boolean postToTwitter;


    @JsonCreator
    public RunKeeperSettings(
            @JsonProperty("facebook_connected") boolean facebookConnected,
            @JsonProperty("twitter_connected") boolean twitterConnected,
            @JsonProperty("post_fitness_activity_facebook") boolean postToFacebook,
            @JsonProperty("post_fitness_activity_twitter") boolean postToTwitter) {
        this.facebookConnected = facebookConnected;
        this.twitterConnected = twitterConnected;
        this.postToFacebook = postToFacebook;
        this.postToTwitter = postToTwitter;
    }
}
