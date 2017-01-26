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

    //Redundant since this sync belongs to a C2RActivity containing the source activity but it feels better... Keep it until something breaks.
    private ExternalActivity sourceActivity;

    private Instant date;

}
