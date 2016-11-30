package se.kumliens.concept2runkeeper.vaadin.views.login;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.annotation.PostConstruct;

import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;

/**
 * Created by svante2 on 2016-11-29.
 */
@SpringView
@Slf4j
@RequiredArgsConstructor
public class LoginView extends VerticalLayout implements View {


    private final AuthenticationProvider authenticationProvider;

    private final MongoUserDetailsService userDetailsService;

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
        tabSheet.addTab(loginForm, "Login");
        tabSheet.setSizeUndefined();

        setSpacing(true);
        addComponent(tabSheet);
    }

    private void onRegister(User user) {
        user.addAuthority(new SimpleGrantedAuthority(Authorities.USER.role));
        userDetailsService.createUser(user);
    }

    private void onLogin(LoginCredentials credentials) {
        try {
            Authentication ud = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
            log.info("User logged in: " + ud);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
            getSession().setAttribute(MainUI.SESSION_ATTR_USER, userDetails);
            Notification.show("Welcome " + ((User) userDetails).getFirstName(), WARNING_MESSAGE);

        } catch (AuthenticationException ae) {
            log.debug("Authentication failed...");
            Notification.show("Sorry, no such username/password combo found", WARNING_MESSAGE);
        } finally {

        }
    }
}
