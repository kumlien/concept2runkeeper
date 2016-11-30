package se.kumliens.concept2runkeeper.vaadin.events;

import lombok.RequiredArgsConstructor;
import se.kumliens.concept2runkeeper.domain.User;

/**
 * Created by svante2 on 2016-11-30.
 */
@RequiredArgsConstructor
public abstract class UserEvent {
    public final User user;
}
