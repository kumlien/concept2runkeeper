package se.kumliens.concept2runkeeper.domain.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by svante2 on 2017-01-02.
 */
@Data
@ToString
public class RunKeeperHeartRate {

    @JsonProperty("timestamp")
    private Double timestamp;

    @JsonProperty("heart_rate")
    private Integer heartRate;
}
