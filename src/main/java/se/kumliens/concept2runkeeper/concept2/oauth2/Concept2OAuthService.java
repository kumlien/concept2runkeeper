package se.kumliens.concept2runkeeper.concept2.oauth2;

import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuth2Authorization;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * Created by svante2 on 2017-01-18.
 */
public class Concept2OAuthService extends OAuth20Service {

    /**
     * Default constructor
     *
     * @param api    OAuth2.0 api information
     * @param config OAuth 2.0 configuration param object
     */
    public Concept2OAuthService(Concept2OAuthApi api, OAuthConfig config) {
        super(api, config);
    }
}
