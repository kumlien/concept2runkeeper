package se.kumliens.concept2runkeeper.vaadin.views.runkeeper;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import lombok.RequiredArgsConstructor;
import org.vaadin.addon.oauthpopup.OAuthCallbackRequestHandler;
import org.vaadin.addon.oauthpopup.OAuthData;
import org.vaadin.addon.oauthpopup.OAuthPopupUI;
import org.vaadin.addon.oauthpopup.URLBasedOAuthPopupOpener;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

/**
 * Opened/created when the user clicks the {@link org.vaadin.addon.oauthpopup.URLBasedButton}
 * In super.init the location of the Page will be set to the configured OAuth auth url.
 * The classes from vaadin-oauthpopup lib creates a redirect_url in a way that the request
 * back to use can be traced back to the correct instance on our side.
 *
 * When the callback arrives this page will be closed by the library executing a java script
 * snippet closing the current page. It will also exchange the verifier token for an access
 * token (if successfull) and then call the listener registered by us (success/failure).
 *
 * Created by svante2 on 2016-12-04.
 */
@SpringUI(path = URLBasedOAuthPopupOpener.UI_URL)
@RequiredArgsConstructor
public class RunKeeperOauthPopupUi extends OAuthPopupUI {

    private final SpringNavigator springNavigator;

    public static final String DATA_PARAM_NAME = "data";

    private OAuthCallbackRequestHandler callbackHandler;

    @Override
    protected void init(VaadinRequest request) {
        springNavigator.navigateTo(MainUI.getNavigatorViewNameBasedOnView(DummyView.class)); //Dummy navigation to make vaadin spring happy
        String attr;
        OAuthData data = null;
        OAuth1RequestToken requestToken = null;
        if ((attr=request.getParameter(DATA_PARAM_NAME)) == null) {
//            throw new IllegalStateException(
//                    String.format("No URI parameter named \"%s\".\n", DATA_PARAM_NAME) +
//                            "Please use OAuthPopupButton or a subclass to open OAuthPopup.");
        } else if ((data = (OAuthData) getSession().getAttribute(attr)) == null) {
            throw new IllegalStateException(
                    String.format("No session attribute named \"%s\" found.\n", attr) +
                            "Please use OAuthPopupButton or a subclass to open OAuthPopup.");
        } else if (!data.isOAuth20()) {
            requestToken = data.createNewRequestToken();
        }

        addCallbackHandler(requestToken, data);
        goToAuthorizationUrl(requestToken, data);
    }

    @Override
    public void detach() {
        super.detach();

        // The session may have been already cleaned up by requestHandler,
        // not always though.
        // Doing it again doesn't do harm (?).
        callbackHandler.cleanUpSession(getSession());
    }

    private void addCallbackHandler(OAuth1RequestToken requestToken, OAuthData data) {
        callbackHandler = new OAuthCallbackRequestHandler(requestToken, data);
        getSession().addRequestHandler(callbackHandler);
    }

    public void removeCallbackHandler() {
        if (callbackHandler!=null) {
            getSession().removeRequestHandler(callbackHandler);
            callbackHandler = null;
        }
    }

    private void goToAuthorizationUrl(OAuth1RequestToken requestToken, OAuthData data) {
        String authUrl = data.getAuthorizationUrl(requestToken);
        //Logger.getGlobal().log(Level.INFO, "Navigating to authorization URL: " + authUrl);
        Page.getCurrent().setLocation(authUrl);
    }
}
