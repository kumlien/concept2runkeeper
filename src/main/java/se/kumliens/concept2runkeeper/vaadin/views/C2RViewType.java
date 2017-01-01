package se.kumliens.concept2runkeeper.vaadin.views;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import se.kumliens.concept2runkeeper.vaadin.views.login.LoginView;
import se.kumliens.concept2runkeeper.vaadin.views.settings.SettingsView;
import se.kumliens.concept2runkeeper.vaadin.views.sync.SynchronizeView;

import java.util.NoSuchElementException;

/**
 * Created by svante2 on 2016-12-31.
 */
public enum  C2RViewType {


    HOME("home", HomeView.class, FontAwesome.HOME),
    HISTORY( "history", HistoryView.class, FontAwesome.BAR_CHART_O),
    SYNCHRONIZE("synchronize",SynchronizeView.class, FontAwesome.ARROWS),
    SETTINGS("settings", SettingsView.class, FontAwesome.FILE_TEXT_O),
    LOGIN("login", LoginView.class, FontAwesome.USER_SECRET);

    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;

    private C2RViewType(final String viewName,
                              final Class<? extends View> viewClass,
                              final Resource icon) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
    }


    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static C2RViewType getByViewName(final String viewName) {
        C2RViewType result = null;
        for (C2RViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
            }
        }
        return result;
    }
}
