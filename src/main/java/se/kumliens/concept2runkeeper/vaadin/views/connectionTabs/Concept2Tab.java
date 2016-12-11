package se.kumliens.concept2runkeeper.vaadin.views.connectionTabs;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import sun.jvm.hotspot.debugger.cdbg.basic.LazyBlockSym;

import javax.annotation.PostConstruct;

/**
 * Created by svante2 on 2016-12-08.
 */
@SpringComponent
@ViewScope
@Slf4j
@RequiredArgsConstructor
public class Concept2Tab extends AbstractConnectionTab {


    protected void doInit() {
        if (!StringUtils.hasText(user.getConcept2AccessToken())) {
            setUpWithMissingAuth();
        } else {
            setUpWithAuthPresent();
        }
    }


    @Override
    protected void setUpWithAuthPresent() {
        tab.setIcon(FontAwesome.CHECK_SQUARE_O);
    }

    @Override
    protected void setUpWithMissingAuth() {
        tab.setIcon(FontAwesome.CHAIN_BROKEN);
        addComponent(new Label("Darn, seems we still miss something here..."));
    }
}
