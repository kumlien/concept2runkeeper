package se.kumliens.concept2runkeeper.vaadin;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.View;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBusListener;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperProps;
import se.kumliens.concept2runkeeper.vaadin.events.UserLoggedInEvent;
import se.kumliens.concept2runkeeper.vaadin.views.*;
import se.kumliens.concept2runkeeper.vaadin.views.login.LoginView;

import javax.annotation.PreDestroy;

import static com.vaadin.ui.themes.ValoTheme.BUTTON_LARGE;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_LINK;

/**
 * Created by svante2 on 2016-11-28.
 */
@Title("C2R")
@SpringUI(path = "test")
@Theme("valo")
@Push(transport = Transport.WEBSOCKET)
@Slf4j
public class MainUI extends UI implements EventBusListener<Object> {

    public static final String SESSION_ATTR_USER = "theUserObject";

    private final RunkeeperProps runkeeperProps;

    private final MainViewDisplay mainViewDisplay;

    private final EventBus.UIEventBus eventBus;

    private Button loginLink; //displayed for non logged in users

    private Button logoutLink; //displayed for logged in users

    private Button homeLink; //displayed for logged in users

    public MainUI(MainViewDisplay mainContent, SpringNavigator navigator, RunkeeperProps runkeeperProps, EventBus.UIEventBus eventBus) {
        this.mainViewDisplay = mainContent;
        this.eventBus = eventBus;
        navigator.setErrorView(ErrorView.class);
        this.runkeeperProps = runkeeperProps;
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this); // It's good manners to do this, even though we should be automatically unsubscribed when the UI is garbage collected
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        eventBus.subscribe(this);
        setContent(
                new MVerticalLayout().add(
                        new Header("Welcome to concept2runkeeper").withStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER),
                new MHorizontalLayout()
                        .add(createNavigationBar())
                        .expand(mainViewDisplay)
                        .withFullHeight().space()
                )
        );

    }

    private Component createNavigationBar() {
        MVerticalLayout m = new MVerticalLayout().withWidth("300px");
        m.addComponent(createNavButton("Welcome", IndexView.class));
        loginLink = createNavButton("Login", LoginView.class);
        m.addComponent(loginLink);
        homeLink = createNavButton("Home", HomeView.class);
        homeLink.setVisible(false);
        m.addComponent(homeLink);

        logoutLink = createNavButton("Logout", LoginView.class);
        logoutLink.setVisible(false);
        m.addComponent(logoutLink);
        return m;
    }

    private Button createNavButton(String linkText, Class<? extends View> theView) {
        MButton button = new MButton().withCaption(linkText).withStyleName(BUTTON_LARGE, BUTTON_LINK);
        button.addClickListener(e -> getNavigator().navigateTo(getNavigatorViewNameBasedOnView(theView)));
        return button;
    }


    @Override
    public void onEvent(org.vaadin.spring.events.Event<Object> event) {
        Object payload = event.getPayload();
        if(payload instanceof UserLoggedInEvent) {
            UserLoggedInEvent userLoggedInEvent = (UserLoggedInEvent) payload;
            log.info("User logged in...");
            loginLink.setVisible(false);
            homeLink.setVisible(true);
            logoutLink.setVisible(true);
            if(userLoggedInEvent.user.lacksPermissions()) {
                getNavigator().navigateTo(getNavigatorViewNameBasedOnView(ConnectView.class));
            } else {
                getNavigator().navigateTo(getNavigatorViewNameBasedOnView(HomeView.class));
            }
        }
    }

    public static final String getNavigatorViewNameBasedOnView(Class<? extends View> theView) {
        return theView.getSimpleName().replaceAll("View", "").toLowerCase();
    }
}
