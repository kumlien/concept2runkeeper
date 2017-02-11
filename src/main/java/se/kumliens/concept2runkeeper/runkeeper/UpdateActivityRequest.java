package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import se.kumliens.concept2runkeeper.domain.runkeeper.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by svante2 on 2016-12-11.
 */
@Builder
@Getter
@ToString
public class UpdateActivityRequest {

    static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyy HH:mm:ss", Locale.US);

    @JsonProperty("type")
    private ActivityType type;

    @JsonProperty("equipment")
    private Equipment equipment;

    @JsonProperty("start_time")
    private Instant startTime;

    //The total distance traveled, in meters
    @JsonProperty("total_distance")
    private String distance;

    //The duration of the activity, in seconds
    @JsonProperty("duration")
    private Double duration;

    @JsonProperty("total_calories")
    private Double totalCalories;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("heart_rate")
    private List<RunKeeperHeartRate> heartRates;

    public String getStartTime() {
        //"start_time": "Sat, 1 Jan 2011 00:00:00",
        return dateFormat.format(new Date(startTime.toEpochMilli()));
    }


    /**
     *
     * @param runkeeperActivity
     * @return A {@link UpdateActivityRequest} which can be used to update a {@link RunkeeperActivity} at RunKeeper
     * @throws ParseException If we fail to parse the start time string to a {@link Date}
     */
    public static UpdateActivityRequest from(RunkeeperActivity runkeeperActivity) throws ParseException {
        return builder()
                .type(runkeeperActivity.getType())
                .startTime(Instant.ofEpochMilli(dateFormat.parse(runkeeperActivity.getStartTime()).getTime()))
                .totalCalories(runkeeperActivity.getTotalCalories())
                .distance(runkeeperActivity.getDistance().toString())
                .duration(runkeeperActivity.getDuration())
                .equipment(runkeeperActivity.getEquipment())
                .heartRates(runkeeperActivity.getHeartRates())
                .notes(runkeeperActivity.getNotes())
                .build();
    }
}
