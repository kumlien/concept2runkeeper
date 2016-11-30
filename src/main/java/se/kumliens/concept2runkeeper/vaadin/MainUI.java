package se.kumliens.concept2runkeeper.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.View;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperProps;
import se.kumliens.concept2runkeeper.vaadin.views.ErrorView;
import se.kumliens.concept2runkeeper.vaadin.views.IndexView;
import se.kumliens.concept2runkeeper.vaadin.views.MainViewDisplay;
import se.kumliens.concept2runkeeper.vaadin.views.login.LoginView;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

/**
 * Created by svante2 on 2016-11-28.
 */
@Title("C2R")
@SpringUI(path = "test")
@Theme("valo")
@Slf4j
public class MainUI extends UI {

    public static final String SESSION_ATTR_USER = "theUserObject";

    private final RunkeeperProps runkeeperProps;

    private final MainViewDisplay mainViewDisplay;

    public MainUI( MainViewDisplay mainContent, SpringNavigator navigator, RunkeeperProps runkeeperProps) {
        this.mainViewDisplay = mainContent;
        navigator.setErrorView(ErrorView.class);
        this.runkeeperProps = runkeeperProps;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        MVerticalLayout layout = new MVerticalLayout();
        ExternalResource externalResource = new ExternalResource(runkeeperProps.getOauth2UrlAuthorize().toString()+"?user=svante");
        MButton login = new MButton("Authorize this app to add activities to Runkeeper");

        BrowserWindowOpener extension = new BrowserWindowOpener(externalResource);
        //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
        extension.setFeatures("width=480,height=650,resizable,scrollbars=yes,status=0,chrome=1,centerscreen=1");
        extension.extend(login);


        setContent(
                new MVerticalLayout().add(
                        new Header("Welcome to concept2runkeeper").withStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER),
                new MHorizontalLayout()
                        .add(createNavigationBar())
                        .expand(mainViewDisplay)
                        .withFullHeight().space()
                )
        );

    }

    private Component createNavigationBar() {
        MVerticalLayout m = new MVerticalLayout().withWidth("300px");
        m.addComponent(createNavButton("Welcome", IndexView.class));
        m.addComponent(createNavButton("Login", LoginView.class));
        return m;
    }

    private Component createNavButton(String linkText, Class<? extends View> theView) {
        Button button = new Button(linkText);
        button.addClickListener(e -> getNavigator().navigateTo(theView.getSimpleName().replaceAll("View", "").toLowerCase()));
        button.addStyleName(ValoTheme.BUTTON_LARGE);
        button.addStyleName(ValoTheme.BUTTON_LINK);
        return button;
    }
}
