package se.kumliens.concept2runkeeper.concept2.oauth2;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.ParameterList;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.Map;

/**
 * Created by svante2 on 2016-12-07.
 */


public class Concept2OAuthApi extends DefaultApi20 {

    @Override
    public String getAccessTokenEndpoint() {
        return "https://log-dev.concept2.com/oauth/access_token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://log-dev.concept2.com/oauth/authorize";
    }

    public String getAuthorizationUrl(OAuthConfig config, Map<String, String> additionalParams) {
        ParameterList parameters = new ParameterList(additionalParams);
        parameters.add("response_type", config.getResponseType());
        parameters.add("client_id", config.getApiKey());
        String callback = config.getCallback();
        if(callback != null) {
            parameters.add("redirect_uri", callback);
        }

        String scope = config.getScope();
        if(scope != null) {
            parameters.add("scope", scope);
        }

        String state = config.getState();
        if(state != null) {
            parameters.add("state", state);
        }

        //return "https://log-dev.concept2.com/oauth/authorize?response_type=code&client_id=JFRvfPB6KS1n8WqWcHnz2hX7DWGRCq0uzHHCmHtV&redirect_uri=http://www.concept2runkeeper.com/popup/OAuthPopupUI&scope=user:read,results:read";
        return parameters.appendTo(getAuthorizationBaseUrl());
    }

    @Override
    public OAuth20Service createService(OAuthConfig config) {
        return new Concept2OAuthService(this, config);
    }
}

