package se.kumliens.concept2runkeeper.vaadin;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.kumliens.concept2runkeeper.vaadin.views.ErrorView;
import se.kumliens.concept2runkeeper.vaadin.views.MainViewDisplay;

/**
 * Created by svante2 on 2016-12-29.
 */
//@Configuration
public class NavigationConfiguration {

    @Bean
    @UIScope
    public SpringNavigator vaadinNavigator(MainUI ui, MainViewDisplay mainViewDisplay, SpringViewProvider viewProvider) {
        SpringNavigator springNavigator = new MySpringNavigator(ui, null, mainViewDisplay, viewProvider);

        springNavigator.setErrorView(ErrorView.class);
        return springNavigator;
    }

    @UIScope
    public static class MySpringNavigator extends SpringNavigator {

        private final ViewProvider viewProvider;

        public MySpringNavigator(UI ui, NavigationStateManager stateManager,
                                 ViewDisplay display, ViewProvider viewProvider) {
            super(ui, stateManager, display);
            this.viewProvider = viewProvider;
        }

        @Override
        public void init(UI ui, NavigationStateManager stateManager,
                         ViewDisplay display) {
            super.init(ui, stateManager, display);
            addProvider(viewProvider);
        }
    }
}
