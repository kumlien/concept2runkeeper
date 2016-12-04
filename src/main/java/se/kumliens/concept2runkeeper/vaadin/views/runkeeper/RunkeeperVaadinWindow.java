package se.kumliens.concept2runkeeper.vaadin.views.runkeeper;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import lombok.RequiredArgsConstructor;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Created by svante2 on 2016-11-28.
 */
@SpringUI(path = "runkeeperAuth")
@Title("Runkeeper authorization")
@Theme("c2r")
@RequiredArgsConstructor
public class RunkeeperVaadinWindow extends UI {

    private final SpringNavigator springNavigator;


    @Override
    protected void init(VaadinRequest request) {

        MVerticalLayout layout = new MVerticalLayout();
        request.getParameterMap().keySet().stream().map(key -> key + " = " + Arrays.toString(request.getParameterMap().get(key))).forEach(s -> layout.addComponent(new MLabel(s)));




        setContent(layout);
        springNavigator.navigateTo(MainUI.getNavigatorViewNameBasedOnView(RunkeeperauthView.class));
    }
}
