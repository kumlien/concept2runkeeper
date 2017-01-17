package se.kumliens.concept2runkeeper.vaadin.events;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import se.kumliens.concept2runkeeper.domain.User;

/**
 * Created by svante2 on 2016-11-30.
 */
@ToString
public class Concept2AuthArrivedEvent extends ApplicationEvent {

    public final OAuth2AccessToken accessToken;

    public final User user;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param user
     * @param accessToken
     */
    public Concept2AuthArrivedEvent(Object source, User user, OAuth2AccessToken accessToken) {
        super(source);
        this.user = user;
        this.accessToken = accessToken;
    }
}
