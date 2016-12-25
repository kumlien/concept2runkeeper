package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.data.util.filter.Not;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.ui.MNotification;
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

import static com.vaadin.server.FontAwesome.USER;
import static com.vaadin.server.FontAwesome.USER_PLUS;
import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;
import static com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_SUCCESS;
import static com.vaadin.ui.themes.ValoTheme.TABSHEET_FRAMED;
import static com.vaadin.ui.themes.ValoTheme.TABSHEET_PADDED_TABBAR;
import static org.vaadin.spring.events.EventScope.*;

/**
 * Created by svante2 on 2016-11-29.
 */
@SpringView
@Slf4j
@RequiredArgsConstructor
public class LoginView extends VerticalLayout implements View {

    public static final String DEFAULT_FORM_FIELD_WIDTH = "250px";

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
        registerForm.setWidth("100%");

        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName(TABSHEET_FRAMED);
        tabSheet.addStyleName(TABSHEET_PADDED_TABBAR);
        tabSheet.addTab(registerForm, "Sign up").setIcon(USER_PLUS);
        tabSheet.addTab(loginForm, "Login").setIcon(USER);
        tabSheet.setWidth("100%");


        MPanel panel = new MPanel("Login here or sign up if you are new to our service").withWidth("80%").withHeight("80%");
        panel.setContent(new MVerticalLayout(tabSheet));

        addComponent(panel);
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
        setSizeFull();
    }

    private void onRegister(User user) {
        if(userDetailsService.userExists(user.getUsername())) {
            Notification.show("That username/email-address is already taken", WARNING_MESSAGE);
            return;
        }
        try {
            user.addAuthority(new SimpleGrantedAuthority(Authorities.USER.role));
            userDetailsService.createUser(user);
            new MNotification("Welcome to concept2runkeeper!<br>Now it's time to configure your connections.").withHtmlContentAllowed(true).withStyleName(NOTIFICATION_SUCCESS).withDelayMsec(2500).display();
            eventBus.publish(SESSION, this, new UserRegisteredEvent(user));
        } catch (Exception e) {
            log.warn("Exception...", e);
            Notification.show(":-(", "Sorry, something went wrong...(" + e.getMessage() + ")", ERROR_MESSAGE);
        }
    }

    private void onLogin(LoginCredentials credentials) {
        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
            final User userDetails = (User) userDetailsService.loadUserByUsername(credentials.getUsername());
            eventBus.publish(SESSION, this, new UserLoggedInEvent(userDetails));
            Notification.show("Welcome " + userDetails.getFirstName(), TRAY_NOTIFICATION );
        } catch (AuthenticationException ae) {
            log.debug("Authentication failed...");
            Notification.show("Sorry, no such username/password combo found", WARNING_MESSAGE);
        }
    }
}
