package se.kumliens.concept2runkeeper.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperProps;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

/**
 * Created by svante2 on 2016-11-28.
 */
@Title("C2R")
@SpringUI(path = "test")
@Theme("valo")
@Slf4j
@RequiredArgsConstructor
public class VaadinApp extends UI {

    private final RunkeeperProps runkeeperProps;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        MVerticalLayout layout = new MVerticalLayout();
        ExternalResource externalResource = new ExternalResource(runkeeperProps.getOauth2UrlAuthorize().toString()+"?user=svante");
        MButton login = new MButton("Authorize this app to add activities to Runkeeper");

        BrowserWindowOpener extension = new BrowserWindowOpener(externalResource);
        //https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Position_and_size_features
        extension.setFeatures("width=480,height=650,resizable,scrollbars=yes,status=0,chrome=1,centerscreen=1");
        extension.extend(login);

        layout.add(login);
        setContent(layout);
    }
}
