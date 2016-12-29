package se.kumliens.concept2runkeeper.domain.events;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

import static se.kumliens.concept2runkeeper.domain.events.EventType.USER_LOGIN;

/**
 * Created by svante2 on 2016-12-29.
 */
@Getter
public class UserLogInEvent extends AbstractApplicationEvent {

    private final EventType eventType = USER_LOGIN;

    public UserLogInEvent(String userId) {
        super(userId, Instant.now());
    }

}
