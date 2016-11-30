package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.fields.EmailField;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import static com.vaadin.server.FontAwesome.LOCK;
import static com.vaadin.server.FontAwesome.USER;

/**
 * Created by svante2 on 2016-04-28.
 */
public class LoginForm extends AbstractForm<LoginCredentials> {

    private EmailField username = (EmailField) new EmailField("Email").withValidator(new EmailValidator("That is not a valid email address"));
    private MPasswordField password = new MPasswordField("Password");

    public LoginForm() {
        setEntity(new LoginCredentials());
        username.setIcon(USER);
        password.setIcon(LOCK);
        getSaveButton().addStyleName(ValoTheme.BUTTON_HUGE);
        getResetButton().addStyleName(ValoTheme.BUTTON_HUGE);
    }

    @Override
    protected Component createContent() {
        focusFirst();
        setSaveCaption("Login");
        getSaveButton().setIcon(FontAwesome.CHECK_CIRCLE);
        getResetButton().setIcon(FontAwesome.CLOSE);
        return new MVerticalLayout(
                new MFormLayout(username,password),
                getToolbar()
        );
    }
}
