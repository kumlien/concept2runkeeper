package se.kumliens.concept2runkeeper.runkeeper;

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

    public final String userID;

    public final String profileResource; //path to the profile resource

    @JsonCreator
    public RunKeeperUser(@JsonProperty(value = "userID") String userID, @JsonProperty("profile") String profileResource) {
        this.userID = userID;
        this.profileResource = profileResource;
    }
}
