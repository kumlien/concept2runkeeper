package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.vaadin.viritin.LazyList;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.events.AbstractApplicationEvent;
import se.kumliens.concept2runkeeper.repos.EventRepo;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.*;
import static com.vaadin.ui.themes.ValoTheme.TEXTFIELD_HUGE;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.CONCEPT2_LOGO;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.RUNKEEPER_LOGO;
import static se.kumliens.concept2runkeeper.vaadin.MainUI.link;

/**
 * View with the history for the user.
 * <p>
 * Created by svante2 on 2016-12-30
 */
@UIScope
@SpringView
@Slf4j
@RequiredArgsConstructor
public class HistoryView extends MVerticalLayout implements View {

    private final EventRepo eventRepo;

    private final MainUI ui;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void init() {
        int pageSize = 2;

        MGrid<AbstractApplicationEvent> grid = new MGrid<>(AbstractApplicationEvent.class).withProperties("timestamp");
        grid.lazyLoadFrom(new LazyList.PagingProvider<AbstractApplicationEvent>() {
            @Override
            public List<AbstractApplicationEvent> findEntities(int firstRow) {
                try {
                    return eventRepo.findAllByUserId(ui.getUser().getEmail(), new PageRequest(firstRow / pageSize, pageSize)).getContent();
                } catch (Exception e) {
                    log.info("darn... ", e);
                    return new ArrayList<>();
                }
            }
        }, new LazyList.CountProvider() {
            @Override
            public int size() {
                return eventRepo.findByUserId(ui.getUser().getEmail()).size();
            }
        }, pageSize);

        Panel panel = new MPanel("Your activity log").withContent(grid);

        add(panel);
    }
}
