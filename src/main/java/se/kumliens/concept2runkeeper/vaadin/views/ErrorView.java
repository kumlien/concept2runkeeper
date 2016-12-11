package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Created by svante2 on 2016-11-29.
 */
@UIScope
@SpringView
public class ErrorView extends VerticalLayout implements View {

    Label message = new Label("");

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(!StringUtils.hasText(event.getViewName())) {
            UI.getCurrent().getNavigator().navigateTo("index");
        } else {
            message.setValue("Sorry, no view matching viewname '" + event.getViewName() + "'");
        }
    }

    @PostConstruct
    public void init() {
        addComponent(message);
    }
}
