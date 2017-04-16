package se.kumliens.concept2runkeeper.vaadin.views.settings;

import com.github.scribejava.core.model.*;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.vaadin.addon.oauthpopup.*;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.concept2.oauth2.Concept2NoOpInjector;
import se.kumliens.concept2runkeeper.concept2.oauth2.Concept2OAuthApi;
import se.kumliens.concept2runkeeper.concept2.Concept2Props;
import se.kumliens.concept2runkeeper.concept2.oauth2.Concept2PopupConfig;
import se.kumliens.concept2runkeeper.domain.concept2.InternalConcept2Data;
import se.kumliens.concept2runkeeper.repos.UserRepo;
import se.kumliens.concept2runkeeper.services.Concept2Service;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.events.Concept2AuthArrivedEvent;

import java.time.Instant;

import static com.vaadin.server.FontAwesome.CHECK_SQUARE_O;
import static com.vaadin.server.FontAwesome.EXCLAMATION_CIRCLE;
import static com.vaadin.shared.ui.ContentMode.HTML;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_ERROR;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_SUCCESS;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.CONCEPT2_ARROW_SMALL;

/**
 * Created by svante2 on 2016-12-08.
 */
@SpringComponent
@ViewScope
@Slf4j
@RequiredArgsConstructor
public class Concept2Tab extends AbstractSettingsTab {

    private static final String POPUP_WINDOW_FEATURES = "resizable,width=400,height=650,left=150,top=150";
    private static final String POPUP_BUTTON_CAPTION = "Connect to Concept2";
    private static final String SUCCESS_NOTIFICATION_CAPTION = "Great, we can now fetch activities from Concept2 for you";
    private static final int SUCCESS_NOTIFICATION_DELAY = 2500;
    private static final String AUTH_MISSING_PANEL_CAPTION = "Time to set-up your Concept2 connection";
    private static final String AUTH_PRESENT_PANEL_CAPTION = "Your Concept2 connection";

    private final Concept2Props props;

    private final Concept2Service concept2Service;

    private final MainUI ui;

    private final EventBus.ApplicationEventBus applicationEventBus;

    private final UserRepo userRepo;


    protected void doInit() {
        if (!StringUtils.hasText(user.getConcept2AccessToken())) { //todo push this check to the User class
            setUpWithMissingAuth();
        } else {
            setUpWithAuthPresent();
        }
    }


    @Override
    protected void setUpWithAuthPresent() {
        removeAllComponents();
        tab.setIcon(CHECK_SQUARE_O);
        MVerticalLayout layout = new MVerticalLayout(new MLabel("Your Concept2 connection is set up!"), getAuthButton());
        Panel panel = new MPanel(AUTH_PRESENT_PANEL_CAPTION).withContent(layout).withFullHeight();
        addComponent(panel);
        setMargin(true);
    }

    @Override
    protected void setUpWithMissingAuth() {
        tab.setIcon(EXCLAMATION_CIRCLE);
        removeAllComponents();
        OAuthPopupButton popupButton = getAuthButton();
        MLabel label = new MLabel("You are not yet connected to Concept2. </br>" +
                "Click the button below to authorize us to read your Concept2 activities").withContentMode(HTML);

        MVerticalLayout layout = new MVerticalLayout(label, popupButton).withSpacing(true).withMargin(true);
        Panel panel = new MPanel(AUTH_MISSING_PANEL_CAPTION).withContent(layout).withFullHeight();
        addComponent(panel);
        tab.setIcon(EXCLAMATION_CIRCLE);
        setMargin(true);
    }

    //Create the runkeeper auth button
    private OAuthPopupButton getAuthButton() {
        //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
        OAuthPopupButton popupButton = new URLBasedButton(new Concept2OAuthApi(), getOAuthConfig(props.getOauth2ClientId(), props.getOauth2ClientSecret(), ui.getSession()));
        popupButton.setPopupWindowFeatures(POPUP_WINDOW_FEATURES);
        popupButton.setCaption(POPUP_BUTTON_CAPTION);
        popupButton.setIcon(CONCEPT2_ARROW_SMALL);

        popupButton.addOAuthListener(new OAuthListener() {
            @Override
            public void authSuccessful(Token token, boolean isOAuth20) {
                getUI().access(() -> {
                    log.info("Auth success");
                    Notification notification = new Notification(SUCCESS_NOTIFICATION_CAPTION);
                    notification.setStyleName(NOTIFICATION_SUCCESS);
                    notification.setDelayMsec(SUCCESS_NOTIFICATION_DELAY);
                    notification.show(Page.getCurrent());

                    OAuth2AccessToken oAuth2Token = (OAuth2AccessToken) token;
                    InternalConcept2Data internalConcept2Data = InternalConcept2Data.builder()
                            .accessToken(oAuth2Token.getAccessToken())
                            .refreshToken(oAuth2Token.getRefreshToken())
                            .tokenExpiryDate(Instant.now().plusSeconds(oAuth2Token.getExpiresIn()))
                            .firstConnected(Instant.now()).build();
                    user.setInternalConcept2Data(internalConcept2Data);
                    user.setConcept2User(concept2Service.getUser(oAuth2Token.getAccessToken()));
                    //ExternalRunkeeperData externalRunkeeperData = runkeeperService.getAllData(oAuth2AccessToken.getAccessToken()).build();
                    //InternalRunKeeperData internalRunKeeperData = InternalRunKeeperData.builder().token(oAuth2AccessToken.getAccessToken()).firstConnected(Instant.now()).lastTimeConnected(Instant.now()).defaultComment(DEFAULT_ACTIVITY_COMMENT).build();
                    //user.setExternalRunkeeperData(externalRunkeeperData);
                    user = userRepo.save(user);

                    log.info("Got a concept2 user: {}", user);
                    ui.setUserInSession(Concept2Tab.this.user);
                    applicationEventBus.publish(this, new Concept2AuthArrivedEvent(this, Concept2Tab.this.user, oAuth2Token));
                    setUpWithAuthPresent();
                });
            }

            @Override
            public void authDenied(String s) {
                getUI().access(() -> {
                    log.info("Access denied: {} with ui {}", s, getUI());
                    Notification notification = new Notification("Access was denied (" + s + ")");
                    notification.setStyleName(NOTIFICATION_ERROR);
                    notification.show(Page.getCurrent());
                    setUpWithMissingAuth();
                });
            }
        });
        return popupButton;
    }

    public static OAuthPopupConfig getOAuthConfig(String apiKey, String apiSecret, VaadinSession session) {
        OAuthPopupConfig config = new Concept2PopupConfig(apiKey, apiSecret)
                .setCallbackParameterName("redirect_uri")
                //.setVerifierParameterName("code")
                .setResponseType("code")
                .setSignatureType(SignatureType.QueryString)
                .setErrorParameterName("error");
        config.setCallbackInjector(new Concept2NoOpInjector());
        return config;
    }
}
