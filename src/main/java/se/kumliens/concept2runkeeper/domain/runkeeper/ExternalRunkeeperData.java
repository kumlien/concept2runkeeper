package se.kumliens.concept2runkeeper.domain.runkeeper;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import se.kumliens.concept2runkeeper.runkeeper.RunKeeperSettings;

/**
 * A collection of data fetched from RunKeeper where each class
 * represents one RunKeeper resource.
 *
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
