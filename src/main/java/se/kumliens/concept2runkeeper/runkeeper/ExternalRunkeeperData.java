package se.kumliens.concept2runkeeper.runkeeper;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by svante2 on 2016-12-16.
 */
@ToString
@Builder(builderClassName = "Builder")
@Getter
public class ExternalRunkeeperData {

    private RunKeeperUser user;

    private RunKeeperProfile profile;

    private RunKeeperSettings settings;

}
