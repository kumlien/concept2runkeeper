package se.kumliens.concept2runkeeper.domain.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Created by svante2 on 2017-01-02.
 */
@Getter
@Setter
@AllArgsConstructor
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
