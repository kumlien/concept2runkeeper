package se.kumliens.concept2runkeeper.domain;

import com.google.common.base.MoreObjects;
import lombok.Data;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import se.kumliens.concept2runkeeper.runkeeper.ExternalRunkeeperData;
import se.kumliens.concept2runkeeper.runkeeper.InternalRunKeeperData;

import java.net.URL;
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

    private InternalRunKeeperData internalRunKeeperData;

    private ExternalRunkeeperData externalRunkeeperData;

    private String concept2AccessToken;

    private String concept2ConnectDate;

    private Collection<GrantedAuthority> authorities = new HashSet<>();

    @PersistenceConstructor
    public User(String email, String firstName, String lastName, String password, String id, Collection<GrantedAuthority> authorities, InternalRunKeeperData internalRunKeeperData, ExternalRunkeeperData externalRunkeeperData, String concept2AccessToken, String concept2ConnectDate) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.internalRunKeeperData = internalRunKeeperData;
        this.externalRunkeeperData = externalRunkeeperData;
        this.concept2AccessToken = concept2AccessToken;
        this.concept2ConnectDate = concept2ConnectDate;
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
        return isEmpty(concept2AccessToken) || internalRunKeeperData == null;
    }

    public boolean hasConnectionTo(Provider provider) {
        switch (provider) {
            case CONCEPT2:
                return StringUtils.hasText(concept2AccessToken);
            case RUNKEEPER:
                return internalRunKeeperData != null && StringUtils.hasText(internalRunKeeperData.getToken());
            default:
                throw new RuntimeException("Unhandled provider " + provider);
        }
    }

    public URL getAnyRunkeeperProfileImage() {
        if(externalRunkeeperData == null || externalRunkeeperData.getProfile() == null) {
            return null;
        }
        if(externalRunkeeperData.getProfile().getNormalPicture() != null) {
            return externalRunkeeperData.getProfile().getNormalPicture();
        }
        if(externalRunkeeperData.getProfile().getMediumPicture() != null) {
            return externalRunkeeperData.getProfile().getMediumPicture();
        }
        if(externalRunkeeperData.getProfile().getLargePicture() != null) {
            return externalRunkeeperData.getProfile().getLargePicture();
        }
        return null;
    }
}
