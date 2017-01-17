package se.kumliens.concept2runkeeper.vaadin.views.settings;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import com.google.common.base.MoreObjects;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
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
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.ui.MNotification;
import se.kumliens.concept2runkeeper.repos.UserRepo;
import se.kumliens.concept2runkeeper.runkeeper.*;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.events.RunkeeperAuthArrivedEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static com.vaadin.server.FontAwesome.*;
import static com.vaadin.server.FontAwesome.CHECK_SQUARE_O;
import static com.vaadin.server.FontAwesome.FACEBOOK;
import static com.vaadin.server.FontAwesome.MAIL_FORWARD;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.BOTTOM_RIGHT;
import static com.vaadin.ui.themes.ValoTheme.*;
import static org.springframework.util.StringUtils.isEmpty;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.CONNECT_TO_RUNKEEPER;

/**
 * Created by svante2 on 2016-12-08.
 */
@SpringComponent
@ViewScope
@RequiredArgsConstructor
@Slf4j
public class RunKeeperTab extends AbstractSettingsTab {

    private static final String DEFAULT_ACTIVITY_COMMENT = "This activity was synchronized from Concept2 using the concept2runkeeper app";

    private final RunkeeperService runkeeperService;

    private final RunkeeperProps runkeeperProps;

    private final MainUI ui;

    private final EventBus.ApplicationEventBus applicationEventBus;

    private final UserRepo userRepo;

    @Override
    protected void doInit() {
        if (user.getInternalRunKeeperData() == null || isEmpty(user.getInternalRunKeeperData().getToken())) { //todo also check if the token is valid?
            setUpWithMissingAuth();
        } else {
            setUpWithAuthPresent();
        }
    }

    protected void setUpWithAuthPresent() {
        removeAllComponents();

        MPanel panelWithGeneralInfo = getPanelWithGeneralInfo();
        addComponent(panelWithGeneralInfo);

        Panel panelWithSettings = getPanelWithSettings();
        addComponent(panelWithSettings);

        setExpandRatio(panelWithSettings, 1.0f);
        setSpacing(true);
        tab.setIcon(CHECK_SQUARE_O);
    }

    //Create the panel with the different settings.
    private Panel getPanelWithSettings() {
        RunKeeperSettings settings = user.getExternalRunkeeperData().getSettings();
        Panel panelWithSettings = new Panel("Settings used when posting activities to RunKeeper");
        MHorizontalLayout settingsLayout = new MHorizontalLayout().withCaption("Default setting for posting to RunKeeper").withMargin(true);

        MTextField defaultComment = new MTextField("Default comment:")
                .withValue(isEmpty(user.getInternalRunKeeperData().getDefaultComment()) ? DEFAULT_ACTIVITY_COMMENT : user.getInternalRunKeeperData().getDefaultComment())
                .withWidth("90%");
        MButton defaultCommentInfoBtn = new MButton().withStyleName(BUTTON_BORDERLESS).withIcon(QUESTION_CIRCLE);
        defaultCommentInfoBtn.addClickListener(evt ->  new MNotification("This comment will be used for activities without comments added by you")
                .withDelayMsec(2000).withStyleName(NOTIFICATION_SMALL).display());

        MCheckBox postToFacebook = new MCheckBox("Post to Facebook").withValue(MoreObjects.firstNonNull(user.getInternalRunKeeperData().getPostToFacebookOverride(), settings.isPostToFacebook())).withIcon(FACEBOOK);
        postToFacebook.setEnabled(settings.isFacebookConnected());
        if(!settings.isFacebookConnected()) {
            postToFacebook.setDescription("This option is disabled since you haven't connected your RunKeeper account to Facebook");
        }

        MCheckBox postToTwitter = new MCheckBox("Post to Twitter").withValue(MoreObjects.firstNonNull(user.getInternalRunKeeperData().getPostToTwitterOverride(), settings.isPostToTwitter())).withIcon(TWITTER);
        postToTwitter.setEnabled(settings.isTwitterConnected());
        if(!settings.isTwitterConnected()) {
            postToTwitter.setDescription("This option is disabled since you haven't connected your RunKeeper account to Twitter");
        }

        MButton save = new MButton(CHECK_CIRCLE, "Save", clk -> {
            user.getInternalRunKeeperData().setDefaultComment(defaultComment.getValue());
            user.getInternalRunKeeperData().setPostToFacebookOverride(postToFacebook.isChecked());
            user.getInternalRunKeeperData().setPostToTwitterOverride(postToTwitter.isChecked());
            userRepo.save(user);
            setUpWithAuthPresent();
            Notification notification = new Notification("Your settings has been updated");
            notification.setDelayMsec(2000);
            notification.setStyleName(NOTIFICATION_SUCCESS);
            notification.show(Page.getCurrent());
        }).withStyleName(BUTTON_FRIENDLY, BUTTON_LARGE);
        settingsLayout.expand(new MFormLayout(new MHorizontalLayout().expand(defaultComment).add(defaultCommentInfoBtn, BOTTOM_RIGHT), postToFacebook, postToTwitter, save).withSizeUndefined());
        panelWithSettings.setContent(settingsLayout);
        return panelWithSettings;
    }

    private MPanel getPanelWithGeneralInfo() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(ui.getLocale()).withZone(ZoneId.systemDefault());
        String firstConnectDate = dateTimeFormatter.format(user.getInternalRunKeeperData().getFirstConnected());
        Label infoText = new MLabel("You've been connected to RunKeeper since " + firstConnectDate + "</br>" +
                "Last time we successfully accessed RunKeeper on your behalf was " + dateTimeFormatter.format(user.getInternalRunKeeperData().getLastTimeConnected())).withContentMode(HTML);
        MButton refreshButton = getRefreshProfileButton("fetch");
        Link link = createProfileLink(user.getExternalRunkeeperData(), " view ");
        MVerticalLayout generalInfoLayout = new MVerticalLayout(infoText,
                new MHorizontalLayout(
                        new MLabel("You can"),
                        link,
                        new MLabel("or"),
                        refreshButton,
                        new MLabel("your profile.")
                ).withSpacing(true).withMargin(false)
        );
        return new MPanel("General info", generalInfoLayout);
    }

    private MButton getRefreshProfileButton(String caption) {
        return new MButton(REFRESH, clk -> {
            ExternalRunkeeperData.Builder builder = runkeeperService.getAllData(user.getInternalRunKeeperData().getToken());
            user.getInternalRunKeeperData().setLastTimeConnected(Instant.now());
            user.setExternalRunkeeperData(builder.build());
            userRepo.save(user);
            Notification notification = new Notification("Your profile was successfully updated from RunKeeper");
            notification.setStyleName(NOTIFICATION_SUCCESS);
            notification.show(Page.getCurrent());
            setUpWithAuthPresent();
        }).withCaption(caption).withStyleName(BUTTON_LINK, BUTTON_SMALL).withDescription("We will fetch your profile from RunKeeper and thereby verify that we still can post activities to your stream");
    }

    private static Link createProfileLink(ExternalRunkeeperData runKeeperData, String text) {
        Link link = new Link(text, new ExternalResource(runKeeperData.getProfile().getProfile()));
        link.setDescription("View your profile on http://www.runkeeper.com (external link)");
        link.setIcon(MAIL_FORWARD);
        link.setStyleName(BUTTON_TINY);
        return link;
    }

    protected void setUpWithMissingAuth() {
        removeAllComponents();
        OAuthPopupButton popupButton = getAuthButton();
        MLabel label = new MLabel("You are not yet connected to RunKeeper. </br>" +
                "Login to RunKeeper by clicking the button below and allow us to post new activities on your behalf.").withContentMode(HTML);

        MVerticalLayout layout = new MVerticalLayout(label, popupButton).withSpacing(true).withMargin(true);
        Panel panel = new MPanel("Time to set-up your RunKeeper connection").withContent(layout);
        addComponent(panel);
        tab.setIcon(EXCLAMATION_CIRCLE);
    }


    //Create the runkeeper auth button
    private OAuthPopupButton getAuthButton() {
        //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
        OAuthPopupButton popupButton = new URLBasedButton(new RunKeeperOAuthApi(), OAuthPopupConfig.getStandardOAuth20Config(runkeeperProps.getOauth2ClientId(), runkeeperProps.getOauth2ClientSecret()));
        popupButton.setPopupWindowFeatures("resizable,width=400,height=650,left=150,top=150");
        popupButton.setIcon(CONNECT_TO_RUNKEEPER);
        popupButton.addStyleName(BUTTON_BORDERLESS);

        popupButton.addOAuthListener(new OAuthListener() {
            @Override
            public void authSuccessful(Token token, boolean isOAuth20) {
                getUI().access(() -> {
                    Notification notification = new Notification("Great, we can now create RunKeeper activities for you");
                    notification.setStyleName(NOTIFICATION_SUCCESS);
                    notification.setDelayMsec(2500);
                    notification.show(Page.getCurrent());

                    OAuth2AccessToken oAuth2AccessToken = (OAuth2AccessToken) token;
                    ExternalRunkeeperData externalRunkeeperData = runkeeperService.getAllData(oAuth2AccessToken.getAccessToken()).build();
                    InternalRunKeeperData internalRunKeeperData = InternalRunKeeperData.builder().token(oAuth2AccessToken.getAccessToken()).firstConnected(Instant.now()).lastTimeConnected(Instant.now()).defaultComment(DEFAULT_ACTIVITY_COMMENT).build();
                    user.setInternalRunKeeperData(internalRunKeeperData);
                    user.setExternalRunkeeperData(externalRunkeeperData);
                    user = userRepo.save(user);
                    ui.setUserInSession(user);
                    applicationEventBus.publish(this, new RunkeeperAuthArrivedEvent(this, user, oAuth2AccessToken));
                    setUpWithAuthPresent();
                });
            }

            @Override
            public void authDenied(String s) {
                Notification notification = new Notification("Access was denied (" + s +")");
                notification.setStyleName(NOTIFICATION_ERROR);
                notification.show(Page.getCurrent());
                setUpWithMissingAuth();
            }
        });
        return popupButton;
    }
}
