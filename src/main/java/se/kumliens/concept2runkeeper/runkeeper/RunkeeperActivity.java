package se.kumliens.concept2runkeeper.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import se.kumliens.concept2runkeeper.domain.ExternalActivity;

import java.net.URI;

/**
 * Created by svante2 on 2016-12-17.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(of = {"uri"})
public class RunkeeperActivity implements ExternalActivity {

    //The fields. Used from the ui when creating the table
    public static final String URI = "uri";

    public static final String USER_ID = "userID";

    public static final String TYPE = "type";

    public static final String EQUIPMENT = "equipment";

    public static final String START_TIME = "startTime";

    public static final String DISTANCE = "distance";

    public static final String DURATION = "duration";

    @JsonProperty("uri")
    private URI uri;

    @JsonProperty("userID")
    private Integer userID;

    @JsonProperty("type")
    private ActivityType type;

    @JsonProperty("equipment")
    private Equipment equipment;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("total_distance")
    private Double distance;

    @JsonProperty("duration")
    private String duration;
}
