package se.kumliens.concept2runkeeper.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Created by svante2 on 2016-12-22.
 */
@Builder
@Getter
public class Synchronization {

    private Provider source;

    private Provider target;

    private Object targetActivity;

    //Redundant since this synch belongs to a C2RActivity containing the source activity but it feels better... Keep it until something breaks.
    private Object sourceActivity;

    private Instant date;

}
