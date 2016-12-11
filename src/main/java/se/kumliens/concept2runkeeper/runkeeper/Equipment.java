package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created by svante2 on 2016-12-11.
 */
@RequiredArgsConstructor(access = PRIVATE)
public enum Equipment {

    TREADMILL("Treadmill"), STATIONARY_BIKE("Stationary Bike"), ELLIPTICAL("Elliptical"), ROW_MACHINE("Row Machine");

    public final String jsonString;

    @JsonValue
    public String getJsonString(){
        return jsonString;
    }
}
