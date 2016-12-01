package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.data.util.filter.Not;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.security.Authorities;
import se.kumliens.concept2runkeeper.security.MongoUserDetailsService;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import se.kumliens.concept2runkeeper.vaadin.events.UserLoggedInEvent;
import se.kumliens.concept2runkeeper.vaadin.events.UserRegisteredEvent;

import javax.annotation.PostConstruct;

import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static org.vaadin.spring.events.EventScope.*;

/**
 * Created by svante2 on 2016-11-29.
 */
@SpringView
@Slf4j
@RequiredArgsConstructor
public class LoginView extends VerticalLayout implements View {


    private final AuthenticationProvider authenticationProvider;

    private final MongoUserDetailsService userDetailsService;

    private final EventBus.UIEventBus eventBus;


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void setup() {
        LoginForm loginForm = new LoginForm();
        loginForm.setSavedHandler(this::onLogin);

        RegisterForm registerForm = new RegisterForm();
        registerForm.setSavedHandler(this::onRegister);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        tabSheet.addTab(registerForm, "Register");
        tabSheet.addTab(loginForm, "Already registered");
        tabSheet.setSizeUndefined();

        setSpacing(true);
        setMargin(true);
        addComponent(tabSheet);
    }

    private void onRegister(User user) {
        if(userDetailsService.userExists(user.getUsername())) {
            Notification.show("That username/email-address is already taken", WARNING_MESSAGE);
            return;
        }
        try {
            user.addAuthority(new SimpleGrantedAuthority(Authorities.USER.role));
            userDetailsService.createUser(user);
            getSession().setAttribute(MainUI.SESSION_ATTR_USER, user);
            Notification.show("Welcome!", "Great, welcome to concept2runkeeper!", WARNING_MESSAGE);
            eventBus.publish(SESSION, this, new UserRegisteredEvent(user));
        } catch (Exception e) {
            log.warn("Exception...", e);
            Notification.show(":-(", "Sorry, something went wrong...(" + e.getMessage() + ")", ERROR_MESSAGE);
        }
    }

    private void onLogin(LoginCredentials credentials) {
        try {
            Authentication ud = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
            log.info("User logged in: " + ud);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
            getSession().setAttribute(MainUI.SESSION_ATTR_USER, userDetails);
            eventBus.publish(SESSION, this, new UserLoggedInEvent((User) userDetails));
            Notification.show("Welcome " + ((User) userDetails).getFirstName(), WARNING_MESSAGE);
        } catch (AuthenticationException ae) {
            log.debug("Authentication failed...");
            Notification.show("Sorry, no such username/password combo found", WARNING_MESSAGE);
        }
    }
}
