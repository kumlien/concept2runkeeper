package se.kumliens.concept2runkeeper.vaadin;

import com.vaadin.annotations.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.services.EventService;
import se.kumliens.concept2runkeeper.vaadin.events.*;
import se.kumliens.concept2runkeeper.vaadin.views.*;
import se.kumliens.concept2runkeeper.vaadin.views.settings.SettingsView;
import se.kumliens.concept2runkeeper.vaadin.views.login.LoginView;
import se.kumliens.concept2runkeeper.vaadin.views.sync.SynchronizeView;

import javax.annotation.PreDestroy;

import java.net.URL;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static com.vaadin.shared.ui.ui.Transport.WEBSOCKET_XHR;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static com.vaadin.ui.themes.ValoTheme.*;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.RUNKEEPER_DEFAULT_PROFILE_ICON;

/**
 * Created by svante2 on 2016-11-28.
 */
@Title("Concept2RunKeeper")
@SpringUI
@Theme("c2r")
@Push(value = PushMode.MANUAL, transport = WEBSOCKET_XHR)
@PreserveOnRefresh
@Slf4j
public class MainUI extends UI {

    private static final boolean NEW_MENU = true;

    private static final String SESSION_ATTR_USER = "theUserObject";

    private final MainViewDisplay mainViewDisplay;

    private final EventBus.UIEventBus eventBus;

    private final EventService eventService;

    private final C2RMenu menu;

    private Image avatar = new Image(null, RUNKEEPER_DEFAULT_PROFILE_ICON);

    private Button loginLink; //displayed for non logged in users

    private Button logoutLink; //displayed for logged in users

    private Button syncLink; //displayed for logged in users

    private Button settingsLink; //displayed for logged in users

    private Button historyLink; //displayed for logged in users

    public MainUI(MainViewDisplay mainContent, SpringNavigator navigator, EventBus.UIEventBus eventBus, SpringViewProvider springViewProvider, EventService eventService, ApplicationContext applicationContext, C2RMenu menu) {
        this.mainViewDisplay = mainContent;
        this.eventBus = eventBus;
        this.eventService = eventService;
        this.menu = menu;
        navigator.setErrorView(ErrorView.class);
        springViewProvider.setAccessDeniedViewClass(HomeView.class);
        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                eventBus.publish(this, new ViewChangedEvent(C2RViewType.getByViewName(event.getViewName())));
            }
        });
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this); // It's good manners to do this, even though we should be automatically unsubscribed when the UI is garbage collected
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);
        eventBus.subscribe(this);

        MHorizontalLayout center = new MHorizontalLayout()
                .add(menu.setup(this))
                .expand(mainViewDisplay)
                .withStyleName(LAYOUT_WELL);
        center.setSizeFull();

        setContent(
                new MVerticalLayout()
                        .expand(center)
                        .withStyleName(LAYOUT_WELL)
                        .withMargin(false)
                        .withSpacing(true)
                        .withFullHeight()
                        .withFullWidth()
        );
    }

    private Component createNavigationBar() {
        MVerticalLayout links = new MVerticalLayout().withWidth("180px").withHeightUndefined();

        avatar.setWidth(70, PIXELS);
        links.add(avatar, Alignment.TOP_CENTER);

        Button welcomeLink = createNavButton("Welcome", HomeView.class);
        links.addComponent(welcomeLink);

        loginLink = createNavButton("Login", LoginView.class);
        links.addComponent(loginLink);

        syncLink = createNavButton("Synchronzie", SynchronizeView.class);
        syncLink.setVisible(false);
        links.addComponent(syncLink);

        settingsLink = createNavButton("Settings", SettingsView.class);
        settingsLink.setVisible(false);
        links.addComponent(settingsLink);

        historyLink = createNavButton("History", HistoryView.class);
        historyLink.setVisible(false);
        //links.addComponent(historyLink);

        logoutLink = createButton("Logout");
        logoutLink.addClickListener(clk -> {
            ConfirmDialog.show(getUI(), "Confirm", "Are you sure you want to logout?", "Yes", "No", e -> {
                if (e.isConfirmed()) {
                    eventBus.publish(this, new UserLoggedOutEvent(getUser()));
                    getSession().setAttribute(SESSION_ATTR_USER, null);
                    Notification.show("Hope to see you soon again!", WARNING_MESSAGE);
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
        log.info("User logged in {}", event);
        eventService.onUserLogIn(userLoggedInEvent.user);
        setUserInSession(userLoggedInEvent.user);
        adjustLinks(true);
        if (userLoggedInEvent.user.lacksPermissions()) {
            getNavigator().navigateTo(getNavigatorViewNameBasedOnView(SettingsView.class));
        } else {
            getNavigator().navigateTo(getNavigatorViewNameBasedOnView(SynchronizeView.class));
        }
    }

    @EventBusListenerMethod
    private void onRegistredUserEvent(org.vaadin.spring.events.Event<UserRegisteredEvent> event) {
        log.info("User registered ");
        UserRegisteredEvent userRegisteredEvent = event.getPayload();
        eventService.onUserRegistration(userRegisteredEvent.user);
        setUserInSession(userRegisteredEvent.user);
        adjustLinks(true);
        getNavigator().navigateTo(getNavigatorViewNameBasedOnView(SettingsView.class));
    }

    @EventBusListenerMethod
    private void onLoggedOutEvent(org.vaadin.spring.events.Event<UserLoggedOutEvent> event) {
        setUserInSession(null);
        UserLoggedOutEvent userLoggedOutEvent = event.getPayload();
        log.info("User {} logged out", userLoggedOutEvent.user);
        eventService.onUserLogOut(userLoggedOutEvent.user);
        adjustLinks(false);
        getNavigator().navigateTo(getNavigatorViewNameBasedOnView(HomeView.class));
    }

    @EventBusListenerMethod
    private void onAccountDeletedEvent(org.vaadin.spring.events.Event<UserAccountDeletedEvent> event) {
        UserAccountDeletedEvent userAccountDeletedEvent = event.getPayload();
        log.info("User account deleted: {}", userAccountDeletedEvent);
        eventService.onUserAccountDeleted(userAccountDeletedEvent.user);
        getSession().setAttribute(SESSION_ATTR_USER, null);
        adjustLinks(false);
        getNavigator().navigateTo(getNavigatorViewNameBasedOnView(HomeView.class));
    }

    @EventBusListenerMethod
    private void onRunkeeperAuthEvent(org.vaadin.spring.events.Event<RunkeeperAuthArrivedEvent> event) {
        RunkeeperAuthArrivedEvent authArrivedEvent = event.getPayload();
        log.info("Runkeeper auth event {}", authArrivedEvent);
        eventService.onRunKeeperAuth(authArrivedEvent.user);
        adjustLinks(true);
    }

    @EventBusListenerMethod
    private void onActivitySyncEvent(org.vaadin.spring.events.Event<ActivitySyncEvent> event) {
        ActivitySyncEvent activitySyncEvent = event.getPayload();
        log.info("Activity sync event {}", activitySyncEvent);
        eventService.onActivitySync(activitySyncEvent.user, activitySyncEvent.activity);
        adjustLinks(true);
    }


    private void adjustLinks(boolean loggedIn) {
        if(NEW_MENU) return;
        loginLink.setVisible(!loggedIn);
        syncLink.setVisible(loggedIn);
        logoutLink.setVisible(loggedIn);
        settingsLink.setVisible(loggedIn);
        historyLink.setVisible(loggedIn);
        if (loggedIn) {
            URL rkImg = getUser().getAnyRunkeeperProfileImage();
            if (rkImg != null) {
                avatar.setSource(new ExternalResource(rkImg));
            } else {
                avatar.setSource(RUNKEEPER_DEFAULT_PROFILE_ICON);
            }
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

    public void setUserInSession(User user) {
        getSession().setAttribute(SESSION_ATTR_USER, user);
    }
}
