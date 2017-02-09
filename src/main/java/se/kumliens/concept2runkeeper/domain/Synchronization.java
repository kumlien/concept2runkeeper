package se.kumliens.concept2runkeeper.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * Represents a specific sync action of an activity, i.e. sync a concept2 activity with runkeeper.
 *
 * Created by svante2 on 2016-12-22.
 */
@Builder
@Getter
@ToString
public class Synchronization {

    private Provider source;

    private Provider target;

    private ExternalActivity targetActivity;

    private Instant date;

}
