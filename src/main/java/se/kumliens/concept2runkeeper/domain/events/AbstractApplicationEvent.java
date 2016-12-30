package se.kumliens.concept2runkeeper.domain.events;


import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Base class for an application event
 *
 * Created by svante2 on 2016-12-29.
 */
@Document(collection = "ApplicationEvents")
@Getter
public abstract class AbstractApplicationEvent {

    //The id (email-address) of the user involved
    @Indexed
    public final String userId;

    public final Instant timestamp;

    protected AbstractApplicationEvent(String userId, Instant timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public abstract EventType getEventType();

}
