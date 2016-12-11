package se.kumliens.concept2runkeeper.domain;

import lombok.Data;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import se.kumliens.concept2runkeeper.runkeeper.RunKeeperData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by svante2 on 2016-11-29.
 */
@Data
@Document
public class User implements UserDetails {

    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private RunKeeperData runKeeperData;

    private String concept2AccessToken;

    private String concept2ConnectDate;

    private Collection<GrantedAuthority> authorities = new HashSet<>();

    @PersistenceConstructor
    public User(String email, String firstName, String lastName, String password, String id, Collection<GrantedAuthority> authorities, RunKeeperData runKeeperData, String concept2AccessToken) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.runKeeperData = runKeeperData;
        this.concept2AccessToken = concept2AccessToken;
        this.id = id;
        this.authorities = authorities;
    }

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addAuthority(GrantedAuthority authority) {
        authorities.add(authority);
    }

    /**
     * @return true if either the concept2 accessToken or the runkeeper accessToken is missing
     */
    public boolean lacksPermissions() {
        return isEmpty(concept2AccessToken) || runKeeperData == null;
    }

    public boolean hasConnectionTo(Provider provider) {
        switch (provider) {
            case CONCEPT2:
                return StringUtils.hasText(concept2AccessToken);
            case RUNKEEPER:
                return runKeeperData != null && StringUtils.hasText(runKeeperData.getToken());
            default:
                throw new RuntimeException("Unhandled provider " + provider);
        }
    }
}
