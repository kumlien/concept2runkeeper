package se.kumliens.concept2runkeeper.vaadin.events;

import se.kumliens.concept2runkeeper.domain.User;

/**
 * Created by svante2 on 2016-11-30.
 */
public class UserLoggedOutEvent extends UserEvent {

    public UserLoggedOutEvent(User user) {
        super(user);
    }
}
