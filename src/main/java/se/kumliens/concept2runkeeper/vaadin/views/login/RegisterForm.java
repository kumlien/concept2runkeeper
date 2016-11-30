package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
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

import lombok.extern.slf4j.Slf4j;
import se.kumliens.concept2runkeeper.domain.User;

import java.util.function.Consumer;

import static com.vaadin.server.FontAwesome.*;

/**
 * Created by svante2 on 2016-04-28.
 */
@Slf4j
public class RegisterForm extends AbstractForm<User> {

    private EmailField email = (EmailField) new EmailField("E-mail (your username)").withValidator(new EmailValidator("Not a valid email address...")).withValidator(new StringLengthValidator("Please specify your first name", 2, 32, false));
    private MTextField firstName = new MTextField("First name").withValidator(new StringLengthValidator("Please specify your first name", 2, 32, false));
    private MTextField lastName = new MTextField("Last name").withValidator(new StringLengthValidator("Please specify your last name", 2, 32, false));
    private MPasswordField password = new MPasswordField("Password").withValidator(new StringLengthValidator("Must contain at least six characters", 6, 32, false));
    private MPasswordField password2 = new MPasswordField("Password").withValidator(new StringLengthValidator("Must contain at least six characters", 6, 32, false));

    private Consumer<BlurEvent> pwdChecker = blurEvent -> {
        String pwd1 = password.getValue();
        String pwd2 = password2.getValue();
        if(!getFieldGroup().isValid()) {
            return;
        }
        getSaveButton().setEnabled(pwd1.equals(pwd2));
    };

    public RegisterForm() {
        setEntity(new User());
        email.setIcon(ENVELOPE);
        email.setRequired(true);
        firstName.setIcon(TAG);
        firstName.setNullRepresentation("Your first name");
        lastName.setIcon(TAG);
        lastName.setNullRepresentation("Your last name");
        password.setIcon(LOCK);
        password.addBlurListener(pwdChecker);

        password2.setIcon(LOCK);
        password2.addBlurListener(evt -> {
            String pwd1 = password.getValue();
            String pwd2 = password2.getValue();
            if(!getFieldGroup().isValid()) {
                return;
            }
            getSaveButton().setEnabled(pwd1.equals(pwd2));
        });

        getSaveButton().addStyleName(ValoTheme.BUTTON_HUGE);
        getResetButton().addStyleName(ValoTheme.BUTTON_HUGE);

    }


    @Override
    protected Component createContent() {
        focusFirst();
        setSaveCaption("Register");
        getSaveButton().setIcon(FontAwesome.CHECK_CIRCLE);
        getResetButton().setIcon(FontAwesome.CLOSE);
        return new MVerticalLayout(
                new MFormLayout(email,firstName, lastName, password, password2),
                getToolbar()
        );
    }
}
