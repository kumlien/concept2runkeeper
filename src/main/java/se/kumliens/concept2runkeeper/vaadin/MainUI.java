package se.kumliens.concept2runkeeper.vaadin;

import com.vaadin.annotations.*;
import com.vaadin.navigator.View;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import lombok.extern.slf4j.Slf4j;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.vaadin.events.UserLoggedInEvent;
import se.kumliens.concept2runkeeper.vaadin.events.UserLoggedOutEvent;
import se.kumliens.concept2runkeeper.vaadin.events.UserRegisteredEvent;
import se.kumliens.concept2runkeeper.vaadin.views.*;
import se.kumliens.concept2runkeeper.vaadin.views.connectionTabs.RunKeeperTab;
import se.kumliens.concept2runkeeper.vaadin.views.login.LoginView;
import se.kumliens.concept2runkeeper.vaadin.views.sync.SyncView;

import javax.annotation.PreDestroy;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static com.vaadin.ui.Alignment.TOP_CENTER;
import static com.vaadin.ui.themes.ValoTheme.*;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.RUNKEEPER_DEFAULT_PROFILE_ICON;

/**
 * Created by svante2 on 2016-11-28.
 */
@Title("C2R")
@SpringUI
@Theme("c2r")
@Push(transport = Transport.WEBSOCKET)
@PreserveOnRefresh
@Slf4j
public class MainUI extends UI {

    public static final String SESSION_ATTR_USER = "theUserObject";

    private final MainViewDisplay mainViewDisplay;

    private final EventBus.UIEventBus eventBus;

    private Image avatar = new Image(null, RUNKEEPER_DEFAULT_PROFILE_ICON);

    private Button loginLink; //displayed for non logged in users

    private Button logoutLink; //displayed for logged in users

    private Button syncLink; //displayed for logged in users

    private Button settingsLink; //displayed for logged in users

    public MainUI(MainViewDisplay mainContent, SpringNavigator navigator, EventBus.UIEventBus eventBus, SpringViewProvider springViewProvider) {
        this.mainViewDisplay = mainContent;
        this.eventBus = eventBus;
        navigator.setErrorView(ErrorView.class);
        springViewProvider.setAccessDeniedViewClass(IndexView.class);
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this); // It's good manners to do this, even though we should be automatically unsubscribed when the UI is garbage collected
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final Header header = new Header("Keeping your workouts in sync");
        header.setSizeUndefined();
        eventBus.subscribe(this);
        MHorizontalLayout center = new MHorizontalLayout()
                .add(createNavigationBar())
                .expand(mainViewDisplay)
                .withStyleName(LAYOUT_WELL);
        center.setSizeFull();

        setContent(
                new MVerticalLayout(header)
                        .expand(center)
                        .withAlign(header, TOP_CENTER)
                        .withStyleName(LAYOUT_WELL)
                        .withMargin(true)
                        .withSpacing(true)
                        .withFullHeight()
                        .withFullWidth()
                        .withCaption("Content")
        );
    }

    private Component createNavigationBar() {
        MVerticalLayout links = new MVerticalLayout().withWidth("180px").withHeightUndefined();

        avatar.setWidth(70, PIXELS);
        links.add(avatar, Alignment.TOP_CENTER);

        Button welcomeLink = createNavButton("Welcome", IndexView.class);
        links.addComponent(welcomeLink);

        loginLink = createNavButton("Login", LoginView.class);
        links.addComponent(loginLink);

        syncLink = createNavButton("Synchronzie", SyncView.class);
        syncLink.setVisible(false);
        links.addComponent(syncLink);

        settingsLink = createNavButton("Settings", SettingsView.class);
        settingsLink.setVisible(false);
        links.addComponent(settingsLink);

        logoutLink = createButton("Logout");
        logoutLink.addClickListener(clk -> {
            ConfirmDialog.show(getUI(), "Confirm", "Are you sure you want to logout?", "Yes", "No", e -> {
                if (e.isConfirmed()) {
                    eventBus.publish(this, new UserLoggedOutEvent(getUser()));
                    getSession().setAttribute(SESSION_ATTR_USER, null);
                    Notification.show("Hope to see you back soon again!", Notification.Type.WARNING_MESSAGE);
                }
            });
        });
        logoutLink.setVisible(false);
        links.addComponent(logoutLink);
        return links;
    }

    private Button createNavButton(String linkText, Class<? extends View> theView) {
        MButton button = createButton(linkText);
        button.addClickListener(e -> getNavigator().navigateTo(getNavigatorViewNameBasedOnView(theView)));
        return button;
    }

    private MButton createButton(String linkText) {
        return new MButton().withCaption(linkText).withStyleName(BUTTON_LARGE, BUTTON_LINK);
    }

    @EventBusListenerMethod
    private void onLoggedInEvent(org.vaadin.spring.events.Event<UserLoggedInEvent> event) {
        UserLoggedInEvent userLoggedInEvent = event.getPayload();
        adjustLinks(true);
        if (userLoggedInEvent.user.lacksPermissions()) {
            getNavigator().navigateTo(getNavigatorViewNameBasedOnView(SettingsView.class));
        } else {
            getNavigator().navigateTo(getNavigatorViewNameBasedOnView(SyncView.class));
        }
    }

    @EventBusListenerMethod
    private void onRegistredUserEvent(org.vaadin.spring.events.Event<UserRegisteredEvent> event) {
        UserRegisteredEvent userRegisteredEvent = event.getPayload();
        log.info("User registered ");
        adjustLinks(true);
        getNavigator().navigateTo(getNavigatorViewNameBasedOnView(SettingsView.class));
    }

    @EventBusListenerMethod
    private void onLoggedOutEvent(org.vaadin.spring.events.Event<UserLoggedOutEvent> event) {
        UserLoggedOutEvent userLoggedOutEvent = event.getPayload();
        log.info("User {} logged out", userLoggedOutEvent.user.getEmail());
        adjustLinks(false);
        getNavigator().navigateTo(getNavigatorViewNameBasedOnView(IndexView.class));
    }

    private void adjustLinks(boolean loggedIn) {
        loginLink.setVisible(!loggedIn);
        syncLink.setVisible(loggedIn);
        logoutLink.setVisible(loggedIn);
        settingsLink.setVisible(loggedIn);
        if(loggedIn) {
            if(null != getUser().getRunKeeperData().getProfile().getNormalPicture()) {
                avatar.setSource(new ExternalResource(getUser().getRunKeeperData().getProfile().getNormalPicture()));
            } else if(null != getUser().getRunKeeperData().getProfile().getMediumPicture()) {
                avatar.setSource(new ExternalResource(getUser().getRunKeeperData().getProfile().getMediumPicture()));
            }
        } else {
            avatar.setSource(RUNKEEPER_DEFAULT_PROFILE_ICON);
        }
    }


    public static final String getNavigatorViewNameBasedOnView(Class<? extends View> theView) {
        return theView.getSimpleName().replaceAll("View", "").toLowerCase();
    }

    public User getUser() {
        return (User) getSession().getAttribute(SESSION_ATTR_USER);
    }


    public static String link(String linkText, String location) {
        return new StringBuilder("<a href=\"").append(location).append("\">").append(linkText).append("</a>").toString();
    }

}
