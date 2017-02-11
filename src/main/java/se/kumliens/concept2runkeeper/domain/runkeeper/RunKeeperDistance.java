package se.kumliens.concept2runkeeper.domain.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Created by svante2 on 2017-01-02.
 */
@Data
@ToString
@Builder(builderClassName = "builder")
public class RunKeeperDistance {

    /**
     * The timestamp in number of seconds since start
     */
    @JsonProperty("timestamp")
    private Double timestamp;

    /**
     * The total distance traveled since the start of the activity, in meters
     */
    @JsonProperty("distance")
    private Double distance;
}
