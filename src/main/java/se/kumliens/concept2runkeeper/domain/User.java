package se.kumliens.concept2runkeeper.domain;

import com.google.common.base.Preconditions;
import lombok.Data;

import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import lombok.ToString;
import se.kumliens.concept2runkeeper.domain.concept2.Concept2User;
import se.kumliens.concept2runkeeper.domain.concept2.InternalConcept2Data;
import se.kumliens.concept2runkeeper.domain.runkeeper.ExternalRunkeeperData;
import se.kumliens.concept2runkeeper.domain.runkeeper.InternalRunKeeperData;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@ToString
public class User implements UserDetails {

    private String id;

    @Indexed(unique = true)
    @Email
    private String email;

    private boolean emailConfirmed = false;

    @NotNull
    @Size(min = 6, max = 128, message = "Your password must contain between 6 and 128 characters")
    private String password;

    @NotNull
    @Size(min = 2, max = 32, message = "Your first name must contain 2-32 characters")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 32, message = "Your last name must contain 2-32 characters")
    private String lastName;

    private InternalRunKeeperData internalRunKeeperData;

    private ExternalRunkeeperData externalRunkeeperData;

    private InternalConcept2Data internalConcept2Data;

    private String concept2AccessToken;

    private String concept2ConnectDate;

    private Collection<GrantedAuthority> authorities = new HashSet<>();

    private Concept2User concept2User;

    @PersistenceConstructor
    public User(String email, String firstName, String lastName, String password, String id, boolean emailConfirmed, Collection<GrantedAuthority> authorities, InternalRunKeeperData internalRunKeeperData, ExternalRunkeeperData externalRunkeeperData, InternalConcept2Data internalConcept2Data, Concept2User concept2User) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.internalRunKeeperData = internalRunKeeperData;
        this.externalRunkeeperData = externalRunkeeperData;
        this.id = id;
        this.authorities = authorities;
        this.emailConfirmed = emailConfirmed;
        this.internalConcept2Data = internalConcept2Data;
        this.concept2User = concept2User;
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

    public void setEmail(String email) {
        Preconditions.checkArgument(StringUtils.hasText(email), "The email address must not be null");
        this.email = email.toLowerCase();
    }
}
