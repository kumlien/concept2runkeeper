package se.kumliens.concept2runkeeper.vaadin.views.runkeeper;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import lombok.RequiredArgsConstructor;
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

    protected void init(VaadinRequest request) {
        springNavigator.navigateTo(MainUI.getNavigatorViewNameBasedOnView(RunkeeperauthView.class)); //Dummy navigation to make vaadin spring happy
        super.init(request);
    }


}
