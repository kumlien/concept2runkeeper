package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.runkeeper.*;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.views.connectionTabs.Concept2Tab;
import se.kumliens.concept2runkeeper.vaadin.views.connectionTabs.RunKeeperTab;

import javax.annotation.PostConstruct;

import static com.vaadin.ui.Alignment.MIDDLE_CENTER;
import static com.vaadin.ui.Alignment.TOP_CENTER;
import static com.vaadin.ui.themes.ValoTheme.TABSHEET_FRAMED;

/**
 * The view where a user can manage his connections to concept2 and runkeeper
 * <p>
 * Created by svante2 on 2016-11-29.
 */
@SpringView
@RequiredArgsConstructor
@Slf4j
public class SettingsView extends MVerticalLayout implements View {

    private final RunKeeperTab runKeeperComponent;

    private final Concept2Tab concept2Component;

    private final MainUI ui;

    private User user;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        log.info("Entering settings view...");
    }

    @PostConstruct
    public void setUp() {
        user = (User) UI.getCurrent().getSession().getAttribute(MainUI.SESSION_ATTR_USER);
        if (user == null) {
            UI.getCurrent().getNavigator().navigateTo(MainUI.getNavigatorViewNameBasedOnView(IndexView.class));
            return;
        }

        Header header = new Header("Manage your connection settings");
        header.setHeaderLevel(2);
        add(header).withAlign(header, TOP_CENTER);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR  );
        tabSheet.addStyleName(TABSHEET_FRAMED);
        TabSheet.Tab runKeeperTab = tabSheet.addTab(runKeeperComponent, "RunKeeper");
        runKeeperComponent.init(runKeeperTab, ui);
        TabSheet.Tab concept2Tab = tabSheet.addTab(this.concept2Component, "Concept2");
        concept2Component.init(concept2Tab, ui);
        tabSheet.setSizeFull();
        expand(tabSheet);

        setSizeFull();

    }




}
