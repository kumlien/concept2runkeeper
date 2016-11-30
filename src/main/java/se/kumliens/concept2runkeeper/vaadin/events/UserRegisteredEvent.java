package se.kumliens.concept2runkeeper.vaadin.events;

import se.kumliens.concept2runkeeper.domain.User;

/**
 * Created by svante2 on 2016-11-30.
 */
public class UserRegisteredEvent extends UserEvent {

    public UserRegisteredEvent(User user) {
        super(user);
    }
}
