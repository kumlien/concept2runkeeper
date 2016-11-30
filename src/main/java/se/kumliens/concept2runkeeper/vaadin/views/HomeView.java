package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

/**
 * The home view for a logged in user
 *
 * Created by svante2 on 2016-11-30.
 */
@SpringView
public class HomeView extends VerticalLayout implements View {

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
