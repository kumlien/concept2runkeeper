package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import se.kumliens.concept2runkeeper.domain.runkeeper.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    //The duration of the activity, in seconds
    @JsonProperty("duration")
    private Double duration;

    //The total distance traveled, in meters
    @JsonProperty("total_distance")
    private Double distance;

    @JsonProperty("total_calories")
    private Double totalCalories;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("post_to_facebook")
    private boolean postToFacebook;

    @JsonProperty("post_to_twitter")
    private boolean postToTwitter;

    @JsonProperty("distance")
    private List<RunKeeperDistance> distances;

    @JsonProperty("heart_rate")
    private List<RunKeeperHeartRate> heartRates;

    public String getStartTime() {
        //"start_time": "Sat, 1 Jan 2011 00:00:00",
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyy HH:mm:ss", Locale.US);
        return dateFormat.format(new Date(startTime.toEpochMilli()));
    }

    public static RecordActivityRequest from(RunkeeperActivity runkeeperActivity, boolean postToFacebook, boolean postToTwitter) {
        return builder()
                .type(runkeeperActivity.getType())
                //.startTime(runkeeperActivity.getStartTime()) Not needed right now.
                .totalCalories(runkeeperActivity.getTotalCalories())
                .distance(runkeeperActivity.getDistance())
                .distances(runkeeperActivity.getDistances())
                .duration(runkeeperActivity.getDuration())
                .equipment(runkeeperActivity.getEquipment())
                .heartRates(runkeeperActivity.getHeartRates())
                .notes(runkeeperActivity.getNotes())
                .postToFacebook(postToFacebook)
                .postToTwitter(postToTwitter)
                .build();
    }
}
