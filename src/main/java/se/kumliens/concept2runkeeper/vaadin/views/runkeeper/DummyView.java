package se.kumliens.concept2runkeeper.vaadin.views.runkeeper;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Dummy view right now...
 *
 * Created by svante2 on 2016-12-04.
 */
@SpringView
public class DummyView extends VerticalLayout implements View {

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void init() {
    }
}
