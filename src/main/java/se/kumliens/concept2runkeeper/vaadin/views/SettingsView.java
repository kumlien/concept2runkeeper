package se.kumliens.concept2runkeeper.vaadin.views;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.OAuthPopupConfig;
import org.vaadin.addon.oauthpopup.URLBasedButton;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.runkeeper.*;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

import javax.annotation.PostConstruct;

import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;

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
    private static final ThemeResource RUNKEEPER_LOGO = new ThemeResource("images/rk-logo.png");
    private static final ThemeResource RUNKEEPER_ICON = new ThemeResource("images/rk-icon.png");

    private final RunkeeperProps runkeeperProps;

    private final EventBus.ApplicationEventBus eventBus;

    private final RunkeeperService runkeeperService;

    private User user;

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

        Header header = new Header("Your connection settings");
        header.setHeaderLevel(2);

        eventBus.subscribe(this);
        OAuthPopupButton popupButton = getRunkeeperAuthButton();

        add(
                new MHorizontalLayout(header), popupButton);
    }



    //Create the runkeeper auth button
    private OAuthPopupButton getRunkeeperAuthButton() {
        //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
        OAuthPopupButton popupButton = new URLBasedButton(new RunKeeperOAuthApi(), OAuthPopupConfig.getStandardOAuth20Config(runkeeperProps.getOauth2ClientId(), runkeeperProps.getOauth2ClientSecret()));
        popupButton.setPopupWindowFeatures("resizable,width=400,height=650,left=150,top=150");
        popupButton.setIcon(CONNECT_TO_RUNKEEPER);
        popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        popupButton.addOAuthListener(new OAuthListener() {
            @Override
            public void authSuccessful(Token token, boolean isOAuth20) {
                getUI().access(() -> {
                    Notification.show("Great, now we can push activities to RunKeeper!", WARNING_MESSAGE);
                    popupButton.setVisible(false);
                    OAuth2AccessToken token1 = (OAuth2AccessToken) token;
                    RunKeeperUser runKeeperUser = runkeeperService.getUser(token1.getAccessToken());
                    RunKeeperProfile runKeeperProfile = runkeeperService.getProfile(token1.getAccessToken());
                    Image image = new Image("", new ExternalResource(runKeeperProfile.getNormalPicture()));
                    add(image);
                });
            }

            @Override
            public void authDenied(String s) {
                log.info("Denied...:{}", s);
            }
        });
        return popupButton;
    }
}
