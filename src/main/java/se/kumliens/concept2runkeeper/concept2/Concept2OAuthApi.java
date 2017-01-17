package se.kumliens.concept2runkeeper.concept2;

import com.github.scribejava.core.builder.api.DefaultApi20;

/**
 * Created by svante2 on 2016-12-07.
 */


public class Concept2OAuthApi extends DefaultApi20 {

    @Override
    public String getAccessTokenEndpoint() {
        return "https://log.concept2.com/oauth/access_token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://log.concept2.com/oauth/authorize";
    }
}

