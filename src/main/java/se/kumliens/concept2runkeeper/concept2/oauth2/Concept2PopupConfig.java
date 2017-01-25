package se.kumliens.concept2runkeeper.concept2.oauth2;

import org.vaadin.addon.oauthpopup.OAuthPopupConfig;

/**
 * Created by svante2 on 2017-01-23.
 */
public class Concept2PopupConfig extends OAuthPopupConfig {

    //TODO Properties...
    private final String callbackUrl = "http://www.concept2runkeeper.com/popup/OAuthPopupUI";

    private final String scope = "user:read,results:read";

    public Concept2PopupConfig(String apiKey, String apiSecret) {
        super(apiKey, apiSecret);
    }

    @Override
    public String getCallbackUrl() {
        return callbackUrl;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getVerifierParameterName() {
        return "code";
    }
}
