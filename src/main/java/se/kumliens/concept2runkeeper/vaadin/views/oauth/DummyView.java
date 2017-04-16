package se.kumliens.concept2runkeeper.vaadin.views.oauth;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.v7.ui.VerticalLayout;

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
