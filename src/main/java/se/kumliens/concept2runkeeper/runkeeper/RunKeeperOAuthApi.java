package se.kumliens.concept2runkeeper.runkeeper;

import com.github.scribejava.core.builder.api.DefaultApi20;

/**
 * Created by svante2 on 2016-12-07.
 */


public class RunKeeperOAuthApi extends DefaultApi20 {

    @Override
    public String getAccessTokenEndpoint() {
        return "https://runkeeper.com/apps/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://runkeeper.com/apps/authorize";
    }
}

