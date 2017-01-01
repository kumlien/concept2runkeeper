package se.kumliens.concept2runkeeper.vaadin;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.UI;
import org.vaadin.addon.oauthpopup.OAuthPopupUI;
import se.kumliens.concept2runkeeper.vaadin.views.HomeView;
import se.kumliens.concept2runkeeper.vaadin.views.login.LoginView;

/**
 * Created by svante2 on 2016-12-09.
 */
@SpringComponent
public class UserLoggedInAccessController implements ViewAccessControl {

    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        if(ui instanceof OAuthPopupUI) {
            return true;
        }
        MainUI mainUI = (MainUI) ui;
        if(mainUI.getUser() != null) {
            return true;
        }

        if(HomeView.class.getSimpleName().toLowerCase().equals(beanName.toLowerCase())) {
            return true;
        }

        if(LoginView.class.getSimpleName().toLowerCase().equals(beanName.toLowerCase())) {
            return true;
        }

        return false;
    }
}
