package se.kumliens.concept2runkeeper.runkeeper;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Created by svante2 on 2016-11-28.
 */
@Title("Runkeeper")
@SpringUI(path = "runkeeper/auth_response")
@Theme("valo")
public class RunkeeperVaadinWindow extends UI {

    @Override
    protected void init(VaadinRequest request) {

        MTextArea field = new MTextArea("Hi");
        field.setValue(request.getPathInfo());

        MVerticalLayout layout = new MVerticalLayout();
        layout.add(field);


        setContent(layout);
    }
}
