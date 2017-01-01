package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;
import javafx.scene.input.KeyCode;
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

import static com.vaadin.event.ShortcutAction.KeyCode.*;
import static com.vaadin.server.FontAwesome.*;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_HUGE;
import static se.kumliens.concept2runkeeper.vaadin.views.login.LoginView.DEFAULT_FORM_FIELD_WIDTH;

/**
 * Created by svante2 on 2016-04-28.
 */
@Slf4j
public class RegistrationForm extends AbstractForm<User> {


    private EmailField email = (EmailField) new EmailField("E-mail").withWidth(DEFAULT_FORM_FIELD_WIDTH);//.withValidator(new BeanValidator(User.class, "email"));
    private MTextField firstName = new MTextField("First name").withWidth(DEFAULT_FORM_FIELD_WIDTH);//.withValidator(new BeanValidator(User.class, "firstName"));
    private MTextField lastName = new MTextField("Last name").withWidth(DEFAULT_FORM_FIELD_WIDTH);//.withValidator(new BeanValidator(User.class, "lastName"));
    private MPasswordField password = new MPasswordField("Password").withWidth(DEFAULT_FORM_FIELD_WIDTH);//.withValidator(new BeanValidator(User.class, "password"));
    private MPasswordField password2 = new MPasswordField("Password").withWidth(DEFAULT_FORM_FIELD_WIDTH);//.withValidator(new BeanValidator(User.class, "password"));

    private FieldEvents.BlurListener pwdChecker = blurEvent -> {
        String pwd1 = password.getValue();
        String pwd2 = password2.getValue();
        if (!getFieldGroup().isValid()) {
            return;
        }
        getSaveButton().setEnabled(pwd1.equals(pwd2));
    };

    public RegistrationForm(TabSheet tabSheet) {
        setEntity(new User()).hideInitialEmpyFieldValidationErrors();
        email.setIcon(ENVELOPE);
        email.setRequired(true);
        email.setNullRepresentation("");
        email.setNullSettingAllowed(false);
        firstName.setIcon(TAG);
        firstName.setNullRepresentation("");
        lastName.setIcon(TAG);
        lastName.setNullRepresentation("");
        password.setIcon(LOCK);
        password.addBlurListener(pwdChecker);

        password2.setIcon(LOCK);
        password2.addBlurListener(pwdChecker);

        getSaveButton().addStyleName(BUTTON_HUGE);
        getResetButton().addStyleName(BUTTON_HUGE);
        email.focus();

        tabSheet.addSelectedTabChangeListener(evt -> {
            if (evt.getComponent().equals(this)) {
                getSaveButton().setClickShortcut(ENTER);
            } else {
                getSaveButton().removeClickShortcut();
            }
        });
    }


    @Override
    protected Component createContent() {
        focusFirst();
        setSaveCaption("Register");
        getSaveButton().setIcon(FontAwesome.CHECK_CIRCLE);
        getResetButton().setIcon(FontAwesome.CLOSE);
        return new MVerticalLayout(
                new MFormLayout(email, firstName, lastName, password, password2),
                getToolbar()
        );

    }
}
