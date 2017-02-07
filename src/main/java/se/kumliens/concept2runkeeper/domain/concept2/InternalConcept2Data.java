package se.kumliens.concept2runkeeper.domain.concept2;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * Created by svante on 2017-02-07.
 */
@ToString
@Getter
@Builder(builderClassName = "Builder")
public class InternalConcept2Data {

    private String accessToken;

    private String refreshToken;

    private Instant tokenExpiryDate;

    private Instant firstConnected;
}