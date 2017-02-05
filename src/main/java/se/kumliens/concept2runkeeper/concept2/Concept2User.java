package se.kumliens.concept2runkeeper.concept2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.ToString;

/**
 * Created by svante on 2017-01-24.
 */


@JsonRootName(value = "data")
@ToString
public class Concept2User {

    private final String id;

    private final String username;

    private final String firstname;

    private final String lastname;

    private final String gender;

    private final String dateOfBirth;

    private final String email;

    private final String country;

    private final String profileImage;

    private final boolean ageRestricted;


    @JsonCreator
    public Concept2User(
            @JsonProperty(value = "id") String id,
            @JsonProperty(value = "username")String username,
            @JsonProperty(value = "first_name")String firstname,
            @JsonProperty(value = "last_name")String lastname,
            @JsonProperty(value = "gender")String gender,
            @JsonProperty(value = "dob")String dateOfBirth,
            @JsonProperty(value = "email")String email,
            @JsonProperty(value = "country")String country,
            @JsonProperty(value = "profile_image")String profileImage,
            @JsonProperty(value = "age_restricted")boolean ageRestricted) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.country = country;
        this.profileImage = profileImage;
        this.ageRestricted = ageRestricted;
    }
}
