package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.button.MButton;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperProps;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

import javax.annotation.PostConstruct;

/**
 * The view where a user can manage his connections to concept2 and runkeeper
 *
 * Created by svante2 on 2016-11-29.
 */
@SpringView
@RequiredArgsConstructor
@Slf4j
public class ConnectView extends VerticalLayout implements View {

    private final RunkeeperProps runkeeperProps;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void setUp() {
        User user = (User)UI.getCurrent().getSession().getAttribute(MainUI.SESSION_ATTR_USER);
        if (!checkUser(user)) {
            return;
        }

        ExternalResource externalResource = new ExternalResource(runkeeperProps.getOauth2UrlAuthorize().toString()+"?id="+user.getId());
        MButton startAuthFlow = new MButton("Authorize");

        BrowserWindowOpener extension = new BrowserWindowOpener(externalResource);
        //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
        extension.setFeatures("width=480,height=650,resizable,scrollbars=yes,status=0,chrome=1,centerscreen=1");
        extension.extend(startAuthFlow);
        addComponent(startAuthFlow);
    }

    private boolean checkUser(User user) {
        if(user == null) {
            log.warn("Something is weird, the connectview was created without a user in the sesssion...");
            UI.getCurrent().getNavigator().navigateTo("error");
            return false;
        }
        return true;
    }
}
