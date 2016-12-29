package se.kumliens.concept2runkeeper.services;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.kumliens.concept2runkeeper.domain.C2RActivity;
import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.domain.events.*;
import se.kumliens.concept2runkeeper.repos.EventRepo;

/**
 * Created by svante2 on 2016-12-29.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepo repo;

    public void onUserRegistration(User user) {
        Observable.just(user)
                .subscribeOn(Schedulers.io())
                .subscribe(evt -> {
                    log.info("Storing a user registration event");
                    repo.save(new UserRegistrationEvent(user.getEmail()));
                });

    }

    public void onUserLogIn(User user) {
        Observable.just(user)
                .subscribeOn(Schedulers.io())
                .subscribe(evt -> {
                    log.info("Storing a login event");
                    repo.save(new UserLogInEvent(user.getEmail()));
                });

    }

    public void onUserLogOut(User user) {
        Observable.just(user)
                .subscribeOn(Schedulers.io())
                .subscribe(evt -> {
                    log.info("Storing a logout event");
                    repo.save(new UserLogOutEvent(user.getEmail()));
                });
    }

    public void onActivitySync(final User user, final C2RActivity activity) {
        Observable.just(user)
                .subscribeOn(Schedulers.io())
                .subscribe(evt -> {
                    log.info("Storing an activity sync event");
                    repo.save(new ActivitySyncEvent(user.getEmail(), activity.getId()));
                });
    }

    public void onRunKeeperAuth(final User user) {
        Observable.just(user)
                .subscribeOn(Schedulers.io())
                .subscribe(evt -> {
                    log.info("Storing a RunKeeper auth event");
                    repo.save(new RunKeeperAuthEvent(user.getEmail()));
                });
    }
}
