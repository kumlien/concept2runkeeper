package se.kumliens.concept2runkeeper.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import se.kumliens.concept2runkeeper.domain.events.AbstractApplicationEvent;

import java.util.List;

/**
 * Created by svante2 on 2016-11-29.
 */
public interface EventRepo extends MongoRepository<AbstractApplicationEvent, String> {

    List<AbstractApplicationEvent> findByUserId(String email);

    void deleteByUserId(String email);

    Page findAllByUserId(String email, Pageable pageRequest);
}
