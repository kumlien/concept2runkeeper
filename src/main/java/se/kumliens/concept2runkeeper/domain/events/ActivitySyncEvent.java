package se.kumliens.concept2runkeeper.domain.events;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

import static se.kumliens.concept2runkeeper.domain.events.EventType.ACTIVITY_SYNC;

/**
 * Created by svante2 on 2016-12-29.
 */
@Getter
public class ActivitySyncEvent extends AbstractApplicationEvent {

    private final EventType eventType = ACTIVITY_SYNC;

    @Field
    private final String activityId;

    public ActivitySyncEvent(String userId, String activityId) {
        super(userId, Instant.now());
        this.activityId = activityId;
    }
}
