package se.kumliens.concept2runkeeper.vaadin.views.settings;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;

import static com.vaadin.server.FontAwesome.CHECK_SQUARE_O;
import static com.vaadin.server.FontAwesome.EXCLAMATION_CIRCLE;
import static com.vaadin.shared.ui.label.ContentMode.HTML;

/**
 * Created by svante2 on 2016-12-08.
 */
@SpringComponent
@ViewScope
@Slf4j
@RequiredArgsConstructor
public class Concept2Tab extends AbstractSettingsTab {


    protected void doInit() {
        if (!StringUtils.hasText(user.getConcept2AccessToken())) { //todo push this check to the User
            setUpWithMissingAuth();
        } else {
            setUpWithAuthPresent();
        }
    }


    @Override
    protected void setUpWithAuthPresent() {
        tab.setIcon(CHECK_SQUARE_O);
    }

    @Override
    protected void setUpWithMissingAuth() {
        tab.setIcon(EXCLAMATION_CIRCLE);
        addComponent(new MPanel("Concept2 settings").withContent(
                new MHorizontalLayout(new MLabel(
                "We are still waiting to get access to the Concept2 api, hopefully it won't take too long." +
                        "</br>In the meantime you can use the file export utility from Concept2 and synchronize your</br>" +
                        "activities using the generated file. Just make sure you have your RunKeeper connection set up first" +
                        "</br>and then click on Synchronize in the menu bar.").withContentMode(HTML)).withMargin(true)));
        setMargin(true);
    }
}
