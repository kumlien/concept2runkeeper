package se.kumliens.concept2runkeeper.security;

import se.kumliens.concept2runkeeper.domain.User;
import se.kumliens.concept2runkeeper.repos.UserRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import java.util.Optional;

/**
 * Created by svante.kumlien on 19.02.16.
 */
public class MongoUserDetailsService extends AbstractUserDetailsAuthenticationProvider implements UserDetailsManager {

    private static final Logger LOG = LoggerFactory.getLogger(MongoUserDetailsService.class);

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    public MongoUserDetailsService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        LOG.info("Checking password for user {}", userDetails.getUsername());
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
            LOG.debug("Authentication failed for user {}: password does not match stored value", authentication.getPrincipal()+"");
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        final Optional<User> dbUser = userRepo.findByEmail(username);
        if(!dbUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return dbUser.get();
    }

    @Override
    public void createUser(UserDetails user) {
        User internalUser = (User) user;
        internalUser.setPassword(encode(user.getPassword()));
        userRepo.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        userRepo.save((User) user);
    }

    @Override
    public void deleteUser(String username) {
        userRepo.deleteByEmail(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        try {
            loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return retrieveUser(username, new UsernamePasswordAuthenticationToken(username, ""));
    }

    public final String encode(String s) {
        return passwordEncoder.encode(s);
    }
}
