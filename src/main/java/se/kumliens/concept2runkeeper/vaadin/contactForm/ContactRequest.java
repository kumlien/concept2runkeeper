package se.kumliens.concept2runkeeper.vaadin.contactForm;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

/**
 * Created by svante2 on 2017-01-02.
 */
@Data
@ToString
public class ContactRequest {

    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String message;
}
