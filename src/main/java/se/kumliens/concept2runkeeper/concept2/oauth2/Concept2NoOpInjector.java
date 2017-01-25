package se.kumliens.concept2runkeeper.concept2.oauth2;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import lombok.RequiredArgsConstructor;
import org.vaadin.addon.oauthpopup.OAuthCallbackInjector;
import org.vaadin.addon.oauthpopup.OAuthData;

import java.util.Enumeration;
import java.util.UUID;

/**
 * Since there is NO way to transport additional data in request to concept2 and get it back in the redirect we simply
 * keep our fingers crossed and hope for no CSRF attacks... see http://www.twobotechnologies.com/blog/2014/02/importance-of-state-in-oauth2.html
 * We save the parameterValue in this instance and return in the {@link #extractParameterFromCallbackRequest(VaadinRequest, String)}.
 *
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
