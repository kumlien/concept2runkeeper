package se.kumliens.concept2runkeeper.domain.runkeeper;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created by svante2 on 2016-12-11.
 */
@RequiredArgsConstructor(access = PRIVATE)
public enum ActivityType {

    RUNNING("Running"), CYCLING("Cycling"), ROWING("Rowing"), CROSS_COUNTRY_SKIING("Cross-Country Skiing");


    public final String jsonString;

    @JsonValue
    public String getJsonString(){
        return jsonString;
    }
}
