package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import static com.vaadin.ui.themes.ValoTheme.LAYOUT_WELL;

/**
 * Created by svante2 on 2016-11-29.
 */
@SpringViewDisplay
@Slf4j
public class MainViewDisplay extends MHorizontalLayout implements ViewDisplay {

    public MainViewDisplay() {
        addStyleName("mainview");
        setHeight(100, Unit.PERCENTAGE);
    }


    @Override
    public void showView(View view) {
        try {
            Component component = (Component) view;
            removeAllComponents();
            addComponent(component);
        } catch (Exception e) {
            log.warn("Exception when displaying view", view);
            try {
                Notification notification = new Notification("Ooops, we encountered an internal error, please reload the page");
                notification.setStyleName(ValoTheme.NOTIFICATION_ERROR);
                notification.show(Page.getCurrent());
            } finally {
                UI.getCurrent().close();
            }
        }
    }
}
