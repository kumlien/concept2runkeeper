package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.net.URL;

/**
 * Created by svante2 on 2016-12-07.
 */
@Getter
@ToString
public class RunKeeperProfile {

    private final String name;

    private final String location;

    private final String gender;

    private final String birthday;

    private final URL profile;

    private final URL smallPicture;

    private final URL normalPicture;


    @JsonCreator
    public RunKeeperProfile(@JsonProperty("name") String name,
                            @JsonProperty("location") String location,
                            @JsonProperty("gender") String gender,
                            @JsonProperty("birthday") String birthday,
                            @JsonProperty("profile") URL profile,
                            @JsonProperty("small_picture") URL smallPicture,
                            @JsonProperty("normal_picture") URL normalPicture) {
        this.name = name;
        this.location = location;
        this.gender = gender;
        this.birthday = birthday;
        this.profile = profile;
        this.smallPicture = smallPicture;
        this.normalPicture = normalPicture;
    }
}
