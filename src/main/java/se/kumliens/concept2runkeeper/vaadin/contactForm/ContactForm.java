package se.kumliens.concept2runkeeper.vaadin.contactForm;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.util.StringUtils;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.User;

import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;
import static com.vaadin.event.ShortcutAction.KeyCode.ESCAPE;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_FRIENDLY;

/**
 * Created by svante2 on 2017-01-02.
 */
public class ContactForm extends AbstractForm<ContactRequest> {

    private MTextField name = new MTextField("Name");

    private MTextField email = new MTextField("Email");

    private MTextArea message = new MTextArea("Message").withRows(10).withFullWidth();

    public ContactForm(User user) {
        setModalWindowTitle("We love feedback!");
        setEntity(new ContactRequest());
        if(user != null) {
            name.setValue(user.getFirstName() + " " + user.getLastName());
            email.setValue(user.getEmail());
        }
        setResetHandler(cr -> {
            this.closePopup();
        });
        getResetButton().setCaption("Close");
        getResetButton().setIcon(FontAwesome.CLOSE);
        getResetButton().setClickShortcut(ESCAPE);

        getSaveButton().setCaption("Send");
        getSaveButton().setStyleName(BUTTON_FRIENDLY);
        getSaveButton().setIcon(FontAwesome.CHECK);
        getSaveButton().setClickShortcut(ENTER);
        setIcon(FontAwesome.COMMENT);
    }

    @Override
    protected Component createContent() {
        if(StringUtils.isEmpty(name.getValue())) {
            name.focus();
        } else {
            message.focus();
        }
        return new MVerticalLayout(
                new MFormLayout(name, email, message).withWidth(""),
                getToolbar()
        ).withWidth("");
    }
}
