package se.kumliens.concept2runkeeper.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Created by svante2 on 2016-12-09.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Provider {

    RUNKEEPER("RunKeeper"), CONCEPT2("Concept2");

    public final String title;
}
