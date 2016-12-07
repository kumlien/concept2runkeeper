package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by svante2 on 2016-12-07.
 */
@Getter
@ToString
public class RunKeeperUser {

    public final String userID;

    public final String profile;

    @JsonCreator
    public RunKeeperUser(@JsonProperty(value = "userID") String userID, @JsonProperty("profile") String profile) {
        this.userID = userID;
        this.profile = profile;
    }
}
