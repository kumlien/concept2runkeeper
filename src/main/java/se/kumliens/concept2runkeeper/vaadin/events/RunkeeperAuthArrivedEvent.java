package se.kumliens.concept2runkeeper.vaadin.events;

import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Created by svante2 on 2016-11-30.
 */
@ToString
public class RunkeeperAuthArrivedEvent extends ApplicationEvent {

    public final String accessToken;

    public final String userId;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param userId
     * @param accessToken
     */
    public RunkeeperAuthArrivedEvent(Object source, String userId, String accessToken) {
        super(source);
        this.userId = userId;
        this.accessToken = accessToken;
    }
}
