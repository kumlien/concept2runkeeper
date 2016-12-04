package se.kumliens.concept2runkeeper.vaadin.views.runkeeper;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import lombok.RequiredArgsConstructor;
import org.vaadin.addon.oauthpopup.OAuthData;
import org.vaadin.addon.oauthpopup.OAuthPopupUI;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

/**
 * Created by svante2 on 2016-12-04.
 */
@SpringUI(path = "popup/OAuthPopupUI")
@RequiredArgsConstructor
public class MyOauthPopupUi extends OAuthPopupUI {

    private final SpringNavigator springNavigator;

    protected void init(VaadinRequest request) {
        springNavigator.navigateTo(MainUI.getNavigatorViewNameBasedOnView(RunkeeperauthView.class));
        super.init(request);
    }


}
