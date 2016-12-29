package se.kumliens.concept2runkeeper.domain.events;

import lombok.Getter;

import java.time.Instant;

import static se.kumliens.concept2runkeeper.domain.events.EventType.USER_DELETED;
import static se.kumliens.concept2runkeeper.domain.events.EventType.USER_REGISTRATION;

/**
 * Created by svante2 on 2016-12-29.
 */
@Getter
public class UserAccountDeletedEvent extends AbstractApplicationEvent {

    private final EventType eventType = USER_DELETED;

    public UserAccountDeletedEvent(String userId) {
        super(userId, Instant.now());
    }

}
