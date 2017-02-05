package se.kumliens.concept2runkeeper.domain.runkeeper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * Representation of the json struct returned from RunKeeper from the /user resource
 *
 * Created by svante2 on 2016-12-07.
 */
@Getter
@ToString
public class RunKeeperUser {

    private final String userID;

    private final String profileResource; //path to the profile resource

    private final String settingsResource;

    @JsonCreator
    public RunKeeperUser(
            @JsonProperty(value = "userID") String userID,
            @JsonProperty("profile") String profileResource,
            @JsonProperty("settings") String settingsResource) {
        this.userID = userID;
        this.profileResource = profileResource;
        this.settingsResource = settingsResource;
    }
}
