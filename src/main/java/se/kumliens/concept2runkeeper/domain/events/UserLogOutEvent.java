package se.kumliens.concept2runkeeper.domain.events;

import lombok.Getter;

import java.time.Instant;

import static se.kumliens.concept2runkeeper.domain.events.EventType.USER_LOGIN;
import static se.kumliens.concept2runkeeper.domain.events.EventType.USER_LOGOUT;

/**
 * Created by svante2 on 2016-12-29.
 */
@Getter
public class UserLogOutEvent extends AbstractApplicationEvent {

    private final EventType eventType = USER_LOGOUT;

    public UserLogOutEvent(String userId) {
        super(userId, Instant.now());
    }

}
