package se.kumliens.concept2runkeeper.domain.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.rjeschke.txtmark.Run;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import se.kumliens.concept2runkeeper.domain.ExternalActivity;
import se.kumliens.concept2runkeeper.vaadin.converters.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by svante2 on 2016-12-17.
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"uri"})
@Slf4j
public class RunkeeperActivity implements ExternalActivity {

    public static final String DATE_PATTERN = "EEE, dd MMM yyyy hh:mm:ss";

    //The fields. Used from the ui when creating the table
    public static final String URI = "uri";

    public static final String USER_ID = "userID";

    public static final String TYPE = "type";

    public static final String EQUIPMENT = "equipment";

    public static final String START_TIME = "startTime";

    public static final String DISTANCE = "distance";

    public static final String DURATION = "duration";

    public static final String HEART_RATE = "heartRate";

    public static final String DISTANCES = "distances";

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("userID")
    private Integer userID;

    @JsonProperty("type")
    private ActivityType type;

    @JsonProperty("equipment")
    private Equipment equipment;

    @JsonProperty("start_time")
    private String startTime;

    public Date getStartTimeAsDate() {
        DateFormat df = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        try {
            return df.parse(getStartTime());
        } catch (ParseException e) {
            log.warn("Unable to parse string value '{}' to a date", getStartTime(), e);
            return null;
        }
    }

    @JsonProperty("total_distance")
    private Double distance;

    @JsonProperty("duration")
    private Double duration;

    @JsonProperty("heartRate")
    private List<RunKeeperHeartRate> heartRates = new ArrayList<>();

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("distance")
    private List<RunKeeperDistance> distances = new ArrayList<>();

    @JsonProperty("total_calories")
    private Double totalCalories;

    protected RunkeeperActivity() {}

    @PersistenceConstructor
    public RunkeeperActivity(String uri, Integer userID, ActivityType type, Equipment equipment, String startTime, Double distance, Double duration, List<RunKeeperHeartRate> heartRates, List<RunKeeperDistance> distances, Double totalCalories) {
        this.uri = uri;
        this.userID = userID;
        this.type = type;
        this.equipment = equipment;
        this.startTime = startTime;
        this.distance = distance;
        this.duration = duration;
        this.heartRates = MoreObjects.firstNonNull(heartRates, new ArrayList<>());
        this.distances = MoreObjects.firstNonNull(distances, new ArrayList<>());
        this.totalCalories = MoreObjects.firstNonNull(totalCalories, 0.0);
    }
}
