package se.kumliens.concept2runkeeper.vaadin.views.connectionTabs;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.OAuthPopupConfig;
import org.vaadin.addon.oauthpopup.URLBasedButton;
import org.vaadin.spring.events.EventBus;
import se.kumliens.concept2runkeeper.repos.UserRepo;
import se.kumliens.concept2runkeeper.runkeeper.*;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.events.RunkeeperAuthArrivedEvent;


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static com.vaadin.server.FontAwesome.MAIL_FORWARD;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_BORDERLESS;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.CONNECT_TO_RUNKEEPER;

/**
 * Created by svante2 on 2016-12-08.
 */
@SpringComponent
@ViewScope
@RequiredArgsConstructor
@Slf4j
public class RunKeeperTab extends AbstractConnectionTab {

    private final RunkeeperService runkeeperService;

    private final RunkeeperProps runkeeperProps;

    private final MainUI ui;

    private final EventBus.ApplicationEventBus applicationEventBus;

    private final UserRepo userRepo;

    @Override
    protected void doInit() {
        if(user.getRunKeeperData() == null) {
            setUpWithMissingAuth();
        } else {
            setUpWithAuthPresent();
        }
    }

    protected void setUpWithAuthPresent() {
        RunKeeperData runKeeperData = user.getRunKeeperData();
        removeAllComponents();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(ui.getLocale()).withZone(ZoneId.systemDefault());
        String firstConnectDate = dateTimeFormatter.format(runKeeperData.getFirstConnected());
        Label label = new Label("Hi " + runKeeperData.getProfile().getName() + ".</br>" +
                "You are happily connected to RunKeeper since " + firstConnectDate + "</br>" +
                "The last time we checked your connection was " + dateTimeFormatter.format(runKeeperData.getLastTimeConnected()));
        label.setContentMode(HTML);
        addComponent(label);
        Link link = createLink(runKeeperData);
        link.setSizeUndefined();
        addComponent(link);
        setExpandRatio(link, 1.0f);

        tab.setIcon(FontAwesome.CHECK_SQUARE_O);
    }

    private static Link createLink(RunKeeperData runKeeperData) {
        Link link = new Link("Take me to my RunKeeper profile!", new ExternalResource(runKeeperData.getProfile().getProfile()));
        link.setDescription("Visit runkeeper.com");
        link.setIcon(MAIL_FORWARD);
        return link;
    }

    protected void setUpWithMissingAuth() {
        removeAllComponents();
        OAuthPopupButton popupButton = getRunkeeperAuthButton();
        addComponent(popupButton);
        tab.setIcon(FontAwesome.CHAIN_BROKEN);
    }


    //Create the runkeeper auth button
    private OAuthPopupButton getRunkeeperAuthButton() {
        //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
        OAuthPopupButton popupButton = new URLBasedButton(new RunKeeperOAuthApi(), OAuthPopupConfig.getStandardOAuth20Config(runkeeperProps.getOauth2ClientId(), runkeeperProps.getOauth2ClientSecret()));
        popupButton.setPopupWindowFeatures("resizable,width=400,height=650,left=150,top=150");
        popupButton.setIcon(CONNECT_TO_RUNKEEPER);
        popupButton.addStyleName(BUTTON_BORDERLESS);

        popupButton.addOAuthListener(new OAuthListener() {
            @Override
            public void authSuccessful(Token token, boolean isOAuth20) {
                getUI().access(() -> {
                    Notification.show("Great, now we can push activities to RunKeeper!", WARNING_MESSAGE);
                    popupButton.setVisible(false);
                    OAuth2AccessToken oAuth2AccessToken = (OAuth2AccessToken) token;
                    RunKeeperUser runKeeperUser = runkeeperService.getUser(oAuth2AccessToken.getAccessToken());
                    RunKeeperProfile runKeeperProfile = runkeeperService.getProfile(oAuth2AccessToken.getAccessToken());
                    RunKeeperData data = RunKeeperData.builder().token(oAuth2AccessToken.getAccessToken()).user(runKeeperUser).profile(runKeeperProfile).lastTimeConnected(Instant.now()).firstConnected(Instant.now()).build();
                    user.setRunKeeperData(data);
                    applicationEventBus.publish(this, new RunkeeperAuthArrivedEvent(this, user, oAuth2AccessToken));
                    userRepo.save(user);
                    setUpWithAuthPresent();
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
