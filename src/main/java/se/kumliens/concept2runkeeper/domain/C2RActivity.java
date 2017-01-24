package se.kumliens.concept2runkeeper.domain;

import com.google.common.base.MoreObjects;
import com.vaadin.ui.ProgressBar;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by svante2 on 2016-12-22.
 */
@Document
@Slf4j
@Builder
@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class C2RActivity {

    private String id;

    @Indexed
    private String userId;

    @Indexed(unique = true) //The id of this activity in the source system. For concept2 we use the timestamp, for RK we use the uri
    private String sourceId;

    private Provider source;

    //Right now one of RunKeeperActivity or Concept2CsvActivity (Concept2)
    private ExternalActivity sourceActivity;

    private Instant imported;

    private Collection<Synchronization> synchronizations = new HashSet<>();

    public Collection<Synchronization> getSynchronizations() {
        if(synchronizations == null) {
            synchronizations = new HashSet<>();
        }
        return synchronizations;
    }
}
