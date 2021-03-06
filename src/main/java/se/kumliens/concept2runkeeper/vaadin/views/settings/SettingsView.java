package se.kumliens.concept2runkeeper.vaadin.views.settings;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.views.HomeView;

import javax.annotation.PostConstruct;

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

    private final AccountSettingsTab accountSettingsComponent;

    private final RunKeeperTab runKeeperComponent;

    private final Concept2Tab concept2Component;

    private final MainUI ui;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void setUp() {
        if (ui.getUser() == null) {
            UI.getCurrent().getNavigator().navigateTo(MainUI.getNavigatorViewNameBasedOnView(HomeView.class));
            return;
        }

        Header header = new Header("Preferences").withStyleName("viewheader");
        header.setHeaderLevel(2);
        add(header).withAlign(header, TOP_CENTER);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        tabSheet.addStyleName(TABSHEET_FRAMED);

        TabSheet.Tab runKeeperTab = tabSheet.addTab(runKeeperComponent, "RunKeeper");
        runKeeperComponent.init(runKeeperTab, ui);

        TabSheet.Tab concept2Tab = tabSheet.addTab(this.concept2Component, "Concept2");
        concept2Component.init(concept2Tab, ui);

        TabSheet.Tab accountTab = tabSheet.addTab(accountSettingsComponent, "Account settings");
        accountSettingsComponent.init(accountTab, ui);

        tabSheet.setSizeFull();
        expand(tabSheet);

        setSizeFull();

    }




}
