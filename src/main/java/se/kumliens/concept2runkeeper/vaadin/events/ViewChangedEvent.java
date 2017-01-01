package se.kumliens.concept2runkeeper.vaadin.events;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import se.kumliens.concept2runkeeper.vaadin.views.C2RViewType;

/**
 * Created by svante2 on 2016-12-31.
 */
@RequiredArgsConstructor
@Getter
public class ViewChangedEvent {

    private final C2RViewType viewType;
}
