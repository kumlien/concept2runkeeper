package se.kumliens.concept2runkeeper.domain.runkeeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Service;

/**
 * Created by svante2 on 2017-01-02.
 */
@Getter
@ToString
@Setter
@Builder(builderClassName = "builder")
public class RunKeeperHeartRate {

    @JsonProperty("timestamp")
    private Double timestamp;

    @JsonProperty("heart_rate")
    private Integer heartRate;
}
