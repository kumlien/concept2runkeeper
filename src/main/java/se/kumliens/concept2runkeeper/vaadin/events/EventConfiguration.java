package se.kumliens.concept2runkeeper.vaadin.events;

import org.atmosphere.cpr.SessionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.support.ApplicationContextEventBroker;

/**
 * Created by svante2 on 2016-11-30.
 */
@Configuration
public class EventConfiguration {

    @Autowired
    EventBus.ApplicationEventBus applicationEventBus;

    /**
     * Forward {@link org.springframework.context.ApplicationEvent}s to the {@link org.vaadin.spring.events.EventBus.ApplicationEventBus}.
     */
    @Bean
    ApplicationContextEventBroker applicationContextEventBroker() {
        return new ApplicationContextEventBroker(applicationEventBus);
    }

    @Bean
    public SessionSupport atmosphereSessionSupport() {
        return new SessionSupport();
    }
}
