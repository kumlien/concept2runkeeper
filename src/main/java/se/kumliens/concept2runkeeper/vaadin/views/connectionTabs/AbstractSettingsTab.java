package se.kumliens.concept2runkeeper.vaadin.views.connectionTabs;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

/**
 * Created by svante2 on 2016-12-09.
 */
public abstract class AbstractSettingsTab extends VerticalLayout {

    protected User user;
    protected TabSheet.Tab tab;

    public void init(TabSheet.Tab tab, MainUI ui) {
        this.tab = tab;
        user = ui.getUser();
        if(user == null) return; //Should allways be there

        setSizeFull();
        setMargin(true);
        setSpacing(true);
        doInit();
    }

    protected abstract void doInit();

    protected abstract void setUpWithAuthPresent();

    protected abstract void setUpWithMissingAuth();
}
