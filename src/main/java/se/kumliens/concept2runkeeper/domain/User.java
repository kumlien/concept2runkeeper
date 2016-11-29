package se.kumliens.concept2runkeeper.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by svante2 on 2016-11-29.
 */
@Data
@Builder
@Document
public class User {

    private String id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String runkeeperAccessToken;

    private String concept2AccessToken;
}
