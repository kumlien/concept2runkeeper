package se.kumliens.concept2runkeeper.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.kumliens.concept2runkeeper.domain.User;

import java.util.Optional;

/**
 * Created by svante2 on 2016-11-29.
 */
public interface UserRepo extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);
}
