package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

/**
 * Created by svante2 on 2016-12-11.
 */
@Builder
@Getter
@ToString
public class RecordActivityRequest {

    @JsonProperty("type")
    private ActivityType type;

    @JsonProperty("equipment")
    private Equipment equipment;

    @JsonProperty("start_time")
    private Instant startTime;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("total_distance")
    private String distance;

    @JsonProperty("total_calories")
    private Double totalCalories;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("post_to_facebook")
    private boolean postToFacebook;

    @JsonProperty("post_to_twitter")
    private boolean postToTwitter;

    public String getStartTime() {
        //"start_time": "Sat, 1 Jan 2011 00:00:00",
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyy HH:mm:ss", Locale.US);
        return dateFormat.format(new Date(startTime.toEpochMilli()));
    }
}
