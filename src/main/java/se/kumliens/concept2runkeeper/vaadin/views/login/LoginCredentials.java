package se.kumliens.concept2runkeeper.vaadin.views.login;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by svante2 on 2016-04-28.
 */
@Data
public class LoginCredentials {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
