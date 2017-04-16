package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.v7.fields.EmailField;
import org.vaadin.viritin.v7.fields.MPasswordField;
import org.vaadin.viritin.v7.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;
import static com.vaadin.server.FontAwesome.*;
import static se.kumliens.concept2runkeeper.vaadin.views.login.LoginView.DEFAULT_FORM_FIELD_WIDTH;

/**
 * Created by svante2 on 2016-04-28.
 */
public class LoginForm extends AbstractForm<LoginCredentials> {

    private EmailField username = (EmailField) new EmailField("Email").withWidth(DEFAULT_FORM_FIELD_WIDTH);
    private MPasswordField password = new MPasswordField("Password").withWidth(DEFAULT_FORM_FIELD_WIDTH);

    public LoginForm(TabSheet tabSheet) {
        setEntity(new LoginCredentials()).hideInitialEmpyFieldValidationErrors();
        username.setIcon(USER);
        password.setIcon(LOCK);
        getSaveButton().addStyleName(ValoTheme.BUTTON_LARGE);
        getResetButton().addStyleName(ValoTheme.BUTTON_LARGE);
        getSaveButton().addStyleName(ValoTheme.BUTTON_PRIMARY);

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
