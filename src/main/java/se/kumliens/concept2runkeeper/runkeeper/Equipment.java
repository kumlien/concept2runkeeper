package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonValue;
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

    public static Equipment from(String type) {
        if(ROW_MACHINE.jsonString.equalsIgnoreCase(type)) return ROW_MACHINE;
        if(TREADMILL.jsonString.equalsIgnoreCase(type)) return TREADMILL;
        if(STATIONARY_BIKE.jsonString.equalsIgnoreCase(type)) return STATIONARY_BIKE;
        if(ELLIPTICAL.jsonString.equalsIgnoreCase(type)) return ELLIPTICAL;
        return null;
    }
}
