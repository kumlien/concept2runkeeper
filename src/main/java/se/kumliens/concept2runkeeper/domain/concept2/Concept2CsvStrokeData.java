package se.kumliens.concept2runkeeper.domain.concept2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.ToString;

/**
 * This is a representation of the stroke data for a specific activity which can be downloaded from the concept2
 * site.
 * The structure is like this:
 * <ul>
 *     <li>Stroke Number</li>
 *     <li>Time (seconds)</li>
 *     <li>Distance (meters)</li>
 *     <li>pace (seconds per 500m)</li>
 *     <li>Stroke rate</li>
 *     <li>Heart rate</li>
 * </ul>
 *
 * The distance and heart rate can be sent to RunKeeper.
 *
 *
 * Created by svante2 on 2017-02-10.
 */
@Getter
@ToString
@JsonPropertyOrder({"Stroke Number", "Time (seconds)", "Distance (meters)", "Pace (seconds per 500m)","Stroke Rate","Heart Rate"})
public class Concept2CsvStrokeData {

    @JsonProperty("Stroke Number")
    private int strokeNumber;

    @JsonProperty("Time (seconds)")
    private double seconds;

    @JsonProperty("Distance (meters)")
    private double distance;

    @JsonProperty("Pace (seconds per 500m)")
    private double pace;

    @JsonProperty("Stroke Rate")
    private int strokeRate;

    @JsonProperty("Heart Rate")
    private double heartRate;
}
