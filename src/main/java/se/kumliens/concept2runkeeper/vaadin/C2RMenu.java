package se.kumliens.concept2runkeeper.vaadin;


import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.label.MLabel;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.domain.events.RunKeeperAuthEvent;
import se.kumliens.concept2runkeeper.vaadin.events.UserLoggedOutEvent;
import se.kumliens.concept2runkeeper.vaadin.events.ViewChangedEvent;
import se.kumliens.concept2runkeeper.vaadin.views.C2RViewType;

import java.net.URL;

import static com.vaadin.ui.Alignment.MIDDLE_CENTER;
import static com.vaadin.ui.themes.ValoTheme.MENU_PART;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.RUNKEEPER_DEFAULT_PROFILE_ICON;

/**
 * Created by svante2 on 2016-12-31.
 */
@SpringComponent
@UIScope
@RequiredArgsConstructor
public class C2RMenu extends com.vaadin.ui.CustomComponent {

    public static final String ID = "c2r-menu";

    private final EventBus.UIEventBus eventBus;

    private MainUI ui;

    private MenuBar.MenuItem settingsItem;

    private Resource avatarResource = RUNKEEPER_DEFAULT_PROFILE_ICON;


    public C2RMenu setup(MainUI ui) {
        this.ui = ui;
        setPrimaryStyleName("valo-menu");
        setId(ID);
        setSizeUndefined();
        setCompositionRoot(buildContent());
        eventBus.subscribe(this);
        return this;
    }

    @EventBusListenerMethod
    public void onViewChange(ViewChangedEvent evt) {
        setCompositionRoot(buildContent());
    }

    @EventBusListenerMethod
    public void onRunKeeperAuth(RunKeeperAuthEvent evt) {
        setCompositionRoot(buildContent());
    }

    private Component buildContent() {
        final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(buildTitle());
        menuContent.addComponent(buildUserMenu());
//        menuContent.addComponent(buildToggleButton());
        menuContent.addComponent(buildMenuItems());

        return menuContent;
    }

    private Component buildTitle() {
        Label logo = new MLabel("Concept2:RunKeeper");
        logo.setSizeUndefined();
        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.setComponentAlignment(logo, MIDDLE_CENTER);
        logoWrapper.addStyleName(ValoTheme.MENU_TITLE);
        return logoWrapper;
    }

    private Component buildUserMenu() {
        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");

        if(ui.getUser() != null) {
            URL rkImg = ui.getUser().getAnyRunkeeperProfileImage();
            avatarResource = rkImg == null ? avatarResource : new ExternalResource(rkImg);
            settingsItem = settings.addItem("", avatarResource, null);
            profileUpdated();
//            settingsItem.addItem("Edit Profile", (MenuBar.Command) selectedItem -> {

//            });
            settingsItem.addItem("Preferences", (MenuBar.Command) selectedItem -> {
                ui.getNavigator().navigateTo(C2RViewType.SETTINGS.getViewName());
            });
            settingsItem.addSeparator();
            settingsItem.addItem("Sign Out", (MenuBar.Command) selectedItem -> {
                eventBus.publish(this, new UserLoggedOutEvent(ui.getUser()));
            });
        }
        return settings;
    }

    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");

        menuItemsLayout.addComponent(new ValoMenuItemButton(C2RViewType.HOME));
        if(ui.getUser() == null) {
            menuItemsLayout.addComponent(new ValoMenuItemButton(C2RViewType.LOGIN));
        } else {
            menuItemsLayout.addComponent(new ValoMenuItemButton(C2RViewType.SYNCHRONIZE));
        }

        return menuItemsLayout;

    }

    public void profileUpdated() {
        User user = ui.getUser();
        if(user != null) {
            settingsItem.setText(user.getFirstName() + " " + user.getLastName());
        }
    }

    public final class ValoMenuItemButton extends Button {

        private static final String STYLE_SELECTED = "selected";

        private final C2RViewType view;

        public ValoMenuItemButton(final C2RViewType view) {
            this.view = view;
            eventBus.subscribe(this);
            setPrimaryStyleName("valo-menu-item");
            setIcon(view.getIcon());
            setCaption(view.getViewName().substring(0, 1).toUpperCase() + view.getViewName().substring(1));
            addClickListener((ClickListener) event -> {
                UI.getCurrent().getNavigator().navigateTo(view.getViewName());
            });
        }

        @EventBusListenerMethod
        public void handleViewChange(final org.vaadin.spring.events.Event<ViewChangedEvent> event) {
            System.out.println(view.getViewClass() + " got an event from " + event.getSource());
            removeStyleName(STYLE_SELECTED);
            if (event.getPayload().getViewType() == view) {
                addStyleName(STYLE_SELECTED);
            }
        }
    }
}
