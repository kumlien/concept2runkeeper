package se.kumliens.concept2runkeeper.vaadin.views.settings;

import com.vaadin.server.FontAwesome;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.ui.MNotification;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.events.UserAccountDeletedEvent;

import static com.vaadin.shared.ui.ContentMode.HTML;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_SUCCESS;

/**
 * Created by svante2 on 2016-12-29.
 */
@SpringComponent
@ViewScope
@RequiredArgsConstructor
@Slf4j
public class AccountSettingsTab extends AbstractSettingsTab {

    private final EventBus.UIEventBus eventBus;

    private final MainUI ui;

    @Override
    protected void doInit() {
        tab.setIcon(FontAwesome.USER);
        MLabel label = new MLabel("You can at any time delete your account here. This action will not delete or even access any data at RunKeeper or Concept2. <br>" +
                "It will delete all your data here at concept2runkeeper such as login credentials and records of synced activities.").withContentMode(HTML).withStyleName(ValoTheme.LABEL_BOLD);
        MButton deleteAccountButton = new MButton("Delete my account!").withStyleName(ValoTheme.BUTTON_DANGER).withStyleName(ValoTheme.BUTTON_LARGE).addClickListener(() -> {
            ConfirmDialog.show(ui, "Confirm", "Really really sure about this?", "Yes", "No", e -> {
                if (e.isConfirmed()) {
                    eventBus.publish(this, new UserAccountDeletedEvent(ui.getUser()));
                    new MNotification("That's it, everything is deleted, hope to see you back soon!").withDelayMsec(2500).withStyleName(NOTIFICATION_SUCCESS).display();
                }
            });
        });

        MVerticalLayout content = new MVerticalLayout(label).expand(deleteAccountButton).withSpacing(true).withMargin(true);
        MPanel panel = new MPanel("General Account settings").withDescription("Settings which applies to your account").withContent(content);
        addComponent(panel);
    }

    @Override
    protected void setUpWithAuthPresent() {

    }

    @Override
    protected void setUpWithMissingAuth() {

    }
}
