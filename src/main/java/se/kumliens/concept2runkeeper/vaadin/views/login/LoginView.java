package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.annotation.SessionScope;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

import static com.vaadin.ui.themes.ValoTheme.TABSHEET_PADDED_TABBAR;

/**
 * Created by svante2 on 2016-11-29.
 */
@SpringView
@Slf4j
public class LoginView extends VerticalLayout implements View {


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void setup() {
        LoginForm loginForm = new LoginForm();
        loginForm.setSavedHandler(e -> {
            log.info("Login attempt: {}", e);
        });
        loginForm.setResetHandler(entity ->  {
            log.info("Login reset: {}", entity);
        });

        RegisterForm registerForm = new RegisterForm();
        registerForm.setSavedHandler(e -> {
            log.info("Register attempt: {}", e);
        });

        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        tabSheet.addTab(registerForm, "Register");
        tabSheet.addTab(loginForm, "Login");
        tabSheet.setSizeUndefined();

        setSpacing(true);
        addComponent(tabSheet);
    }
}
