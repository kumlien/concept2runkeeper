package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.MBeanFieldGroup;
import org.vaadin.viritin.fields.EmailField;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;

import static com.vaadin.server.FontAwesome.*;

/**
 * Created by svante2 on 2016-04-28.
 */
public class RegisterForm extends AbstractForm<User> {

    private EmailField email = (EmailField) new EmailField("E-mail (your username)").withValidator(new EmailValidator("Not a valid email address..."));
    private MTextField firstName = new MTextField("First name").withValidator(new StringLengthValidator("Please specify your first name", 2, 32, false));
    private MTextField lastName = new MTextField("Last name").withValidator(new StringLengthValidator("Please specify your last name", 2, 32, false));
    private MPasswordField password1 = new MPasswordField("Password").withValidator(new StringLengthValidator("Must contain at least six characters", 6, 32, false));
    private MPasswordField password2 = new MPasswordField("Password").withValidator(new StringLengthValidator("Must contain at least six characters", 6, 32, false));

    public RegisterForm() {
        setEntity(User.builder().build());
        email.setIcon(ENVELOPE);
        firstName.setIcon(TAG);
        firstName.setNullRepresentation("Your first name");
        lastName.setIcon(TAG);
        lastName.setNullRepresentation("Your last name");
        password1.setIcon(LOCK);
        password1.setEagerValidation(true);
        password2.setIcon(LOCK);
        password2.setEagerValidation(true);
        getSaveButton().addStyleName(ValoTheme.BUTTON_HUGE);
        getResetButton().addStyleName(ValoTheme.BUTTON_HUGE);
        setEagerValidation(true);
    }


    @Override
    protected Component createContent() {
        focusFirst();
        setSaveCaption("Register");
        getSaveButton().setIcon(FontAwesome.CHECK_CIRCLE);
        getResetButton().setIcon(FontAwesome.CLOSE);
        return new MVerticalLayout(
                new MFormLayout(email,firstName, lastName, password1, password2),
                getToolbar()
        );
    }
}
