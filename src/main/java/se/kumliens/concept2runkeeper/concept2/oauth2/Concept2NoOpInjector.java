package se.kumliens.concept2runkeeper.concept2.oauth2;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import lombok.RequiredArgsConstructor;
import org.vaadin.addon.oauthpopup.OAuthCallbackInjector;
import org.vaadin.addon.oauthpopup.OAuthData;

import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by svante2 on 2017-01-23.
 */
@RequiredArgsConstructor
public class Concept2NoOpInjector implements OAuthCallbackInjector {

    private String id;

    @Override
    public String injectParameterToCallback(String callback, String parameterKey, String parameterValue) {
        id = parameterValue;
        return callback;
    }

    @Override
    public String extractParameterFromCallbackRequest(VaadinRequest request, String parameterKey) {
        return id;
    }
}
