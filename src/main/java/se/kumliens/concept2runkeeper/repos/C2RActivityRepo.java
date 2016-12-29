package se.kumliens.concept2runkeeper.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.kumliens.concept2runkeeper.domain.C2RActivity;
import se.kumliens.concept2runkeeper.domain.User;

import java.util.Optional;

/**
 * Created by svante2 on 2016-11-29.
 */
public interface C2RActivityRepo extends MongoRepository<C2RActivity, String> {

    C2RActivity findBySourceId(String sourceId);

    void deleteByUserId(String email);
}
