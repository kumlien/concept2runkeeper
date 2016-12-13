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

import static com.vaadin.server.FontAwesome.*;
import static se.kumliens.concept2runkeeper.vaadin.views.login.LoginView.DEFAULT_FORM_FIELD_WIDTH;

/**
 * Created by svante2 on 2016-04-28.
 */
public class LoginForm extends AbstractForm<LoginCredentials> {

    private EmailField username = (EmailField) new EmailField("Email").withWidth(DEFAULT_FORM_FIELD_WIDTH).withValidator(new EmailValidator("That is not a valid email address"));
    private MPasswordField password = new MPasswordField("Password").withWidth(DEFAULT_FORM_FIELD_WIDTH);

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
        getSaveButton().setIcon(CHECK_CIRCLE);
        getSaveButton().setStyleName("primary default");
        getResetButton().setIcon(CLOSE);
        return new MVerticalLayout(
                new MFormLayout(username,password),
                getToolbar()
        );
    }
}
