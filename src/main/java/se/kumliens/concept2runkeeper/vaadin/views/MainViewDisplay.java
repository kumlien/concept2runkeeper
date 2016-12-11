package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import static com.vaadin.ui.themes.ValoTheme.LAYOUT_WELL;

/**
 * Created by svante2 on 2016-11-29.
 */
@SpringViewDisplay
public class MainViewDisplay extends MHorizontalLayout implements ViewDisplay {

    public MainViewDisplay() {
        setHeight(100, Unit.PERCENTAGE);
    }


    @Override
    public void showView(View view) {
        Component component = (Component) view;
        removeAllComponents();
        addComponent(component);


    }
}
