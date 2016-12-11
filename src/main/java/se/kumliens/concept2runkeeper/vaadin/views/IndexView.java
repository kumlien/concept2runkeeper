package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.label.MLabel;

import javax.annotation.PostConstruct;

/**
 * The first page of the app. The {@link ErrorView} routes to this view if request
 * for root comes in.
 *
 * Created by svante2 on 2016-11-29.
 */
@UIScope
@SpringView
public class IndexView extends VerticalLayout implements View {

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void init() {
        addComponent(
                new MLabel("Welcome to the workout synchronizer. This is a tool used to synchronize activities from (at the moment) your Concept2 logbook to you RunKeeper account. " +
                        "Start by register yourself as a user and after that you can give us authorization to read activities from Concept2 and add them as new " +
                        "activites at RunKeeper.")
                    .withStyleName(ValoTheme.TEXTFIELD_HUGE)
        );
    }
}
