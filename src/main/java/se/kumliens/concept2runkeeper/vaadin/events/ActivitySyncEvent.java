package se.kumliens.concept2runkeeper.vaadin.events;

import se.kumliens.concept2runkeeper.domain.C2RActivity;
import se.kumliens.concept2runkeeper.domain.User;

/**
 * Created by svante2 on 2016-12-29.
 */
public class ActivitySyncEvent extends UserEvent {

    public final C2RActivity activity;

    public ActivitySyncEvent(User user, C2RActivity activity) {
        super(user);
        this.activity = activity;
    }
}
