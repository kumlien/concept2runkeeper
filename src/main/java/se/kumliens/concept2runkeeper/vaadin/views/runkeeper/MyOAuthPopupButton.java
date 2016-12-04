package se.kumliens.concept2runkeeper.vaadin.views.runkeeper;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ThemeResource;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.OAuthPopupConfig;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperProps;

/**
 * Created by svante2 on 2016-12-04.
 */
public class MyOAuthPopupButton extends OAuthPopupButton {


    public MyOAuthPopupButton(DefaultApi20 api, RunkeeperProps runkeeperProps) {
        super(api, OAuthPopupConfig.getStandardOAuth20Config(runkeeperProps.getOauth2ClientId(), runkeeperProps.getOauth2ClientSecret()), "popup/OAuthPopupUI");
        setIcon(new ThemeResource("images/connectToRunKeeper.png"));
    }
}
