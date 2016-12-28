package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import jdk.internal.dynalink.support.BottomGuardingDynamicLinker;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.vaadin.C2RThemeResources;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.converters.RunKeeperDateConverter;

import javax.annotation.PostConstruct;

import java.net.MalformedURLException;
import java.net.URL;

import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.*;
import static com.vaadin.ui.themes.ValoTheme.*;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.CONCEPT2_LOGO;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.RUNKEEPER_ICON;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.RUNKEEPER_LOGO;
import static se.kumliens.concept2runkeeper.vaadin.MainUI.link;

/**
 * The first page of the app. The {@link ErrorView} routes to this view if request
 * for root comes in.
 *
 * Created by svante2 on 2016-11-29.
 */
@UIScope
@SpringView
public class IndexView extends MVerticalLayout implements View {

    private static URL C2_URL;
    private static URL RK_URL;

    {
        try {
            C2_URL = new URL("http://www.concept2.com");
            RK_URL = new URL("https://www.runkeeper.com");

        } catch (MalformedURLException e) {
            //won't happen...
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void init() {
        Header header = new Header("Welcome to the workout synchronizer!").withStyleName(TEXTFIELD_HUGE);
        header.setSizeUndefined();
        header.setHeaderLevel(3);
        MHorizontalLayout top = new MHorizontalLayout(header);
        top.setComponentAlignment(header, MIDDLE_CENTER);

        MHorizontalLayout body = new MHorizontalLayout();

        MLabel left = new MLabel("This is a tool where you can synchronize your activities from your " + link("Concept2 logbook", "http://log.concept2.com/log") +
                " to you " + link("RunKeeper account", "https://runkeeper.com/home") + ". Start by " +link("register", "#!login") +
                " yourself as a user. Then you can give us authorization to read activities from Concept2 and add them as new activites at RunKeeper.").withStyleName(TEXTFIELD_HUGE).withContentMode(HTML);

        MLabel right = new MLabel("This service is still at a very early stage! We currently do not have the possibility to fetch activities" +
                " automatically from Concept2. Instead we offer the functionality to synchronize Concept2 activities based on the file you can " +
                "download from your Concept2 " + link("history page", "http://log.concept2.com/history") + ".").withStyleName(TEXTFIELD_HUGE).withContentMode(HTML);

        Link c2Link = new Link(null, new ExternalResource(C2_URL), "_self", 0, 0, BorderStyle.NONE);
        c2Link.setIcon(CONCEPT2_LOGO);

        Link rkLink = new Link(null, new ExternalResource(RK_URL), "_self", 0, 0, BorderStyle.NONE);
        rkLink.setIcon(RUNKEEPER_LOGO);

        body.add(new MVerticalLayout(left, c2Link).withAlign(c2Link, BOTTOM_CENTER).withHeight("100%"),
                new MVerticalLayout(right,rkLink).withAlign(rkLink, BOTTOM_CENTER).withHeight("100%"));
        body.withWidth("100%").withHeight("100%");
        MPanel panel = new MPanel(body);
        panel.setWidth("90%");

        add(top);
        expand(panel);
        withAlign(top, TOP_CENTER);
        withAlign(panel, BOTTOM_CENTER);
    }
}
