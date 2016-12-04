package se.kumliens.concept2runkeeper.vaadin.views;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Token;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.modeler.NotificationInfo;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.OAuthPopupConfig;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperProps;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.events.RunkeeperAuthArrivedEvent;
import se.kumliens.concept2runkeeper.vaadin.views.runkeeper.MyOAuthPopupButton;

import javax.annotation.PostConstruct;

/**
 * The view where a user can manage his connections to concept2 and runkeeper
 * <p>
 * Created by svante2 on 2016-11-29.
 */
@SpringView
@RequiredArgsConstructor
@Slf4j
public class SettingsView extends MVerticalLayout implements View {

    private static final ThemeResource CONNECT_TO_RUNKEEPER = new ThemeResource("images/connectToRunKeeper.png");

    private final RunkeeperProps runkeeperProps;

    private final EventBus.ApplicationEventBus eventBus;

    private User user;

    private MButton startAuthFlow;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        log.info("Entering settings view...");
    }

    @PostConstruct
    public void setUp() {
        user = (User) UI.getCurrent().getSession().getAttribute(MainUI.SESSION_ATTR_USER);
        if (user == null) {
            UI.getCurrent().getNavigator().navigateTo(MainUI.getNavigatorViewNameBasedOnView(IndexView.class));
            return;
        }

        Header header = new Header("Your connection setting");
        header.setHeaderLevel(2);

        startAuthFlow = createConnectToRunkeeperButton(user);
        eventBus.subscribe(this);

        OAuthPopupButton popupButton = new MyOAuthPopupButton(new RunkeeperApi(runkeeperProps), runkeeperProps);
        popupButton.setPopupWindowFeatures("resizable,width=400,height=300");
        popupButton.addOAuthListener(new OAuthListener() {
            @Override
            public void authSuccessful(Token token, boolean b) {
                getUI().access(() -> {
                    Notification.show("Thanks", "Great, now we can push activities to RunKeeper!", Notification.Type.WARNING_MESSAGE);
                    popupButton.setVisible(false);
                });
            }

            @Override
            public void authDenied(String s) {
                log.info("Denied...:{}", s);
            }
        });

        Label cogs = new Label();
        cogs.setIcon(FontAwesome.COGS);
        add(
                new MHorizontalLayout(cogs, header), popupButton);
    }

    //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
    private MButton createConnectToRunkeeperButton(User user) {
        String url = runkeeperProps.getOauth2UrlAuthorize().toString() + "&state=" + user.getId();
        ExternalResource externalResource = new ExternalResource(url);
        MButton button = new MButton().withIcon(CONNECT_TO_RUNKEEPER).withStyleName(ValoTheme.BUTTON_LINK);

        BrowserWindowOpener extension = new BrowserWindowOpener("popup/OAuthPopupUI");
        extension.setFeatures("width=480,height=650,resizable=0,scrollbars=0,status=0,chrome=yes,centerscreen=yes");
        extension.extend(button);
        return button;
    }


    @EventBusListenerMethod
    private void onRunkeeperAuthEvent(RunkeeperAuthArrivedEvent evt) {
        log.info("Got a auth event: {}", evt);
        if(evt.userId.equals(user.getId())) {
            log.info("It's for us!");
            startAuthFlow.setVisible(false);
            UI ui = UI.getCurrent();
        }
    }

    @RequiredArgsConstructor
    private static class RunkeeperApi extends DefaultApi20 {

        private final RunkeeperProps runkeeperProps;

        @Override
        public String getAccessTokenEndpoint() {
            return runkeeperProps.getOauth2UrlToken().toString();
        }

        @Override
        protected String getAuthorizationBaseUrl() {
            return "https://runkeeper.com/apps/authorize";
        }
    }
}
