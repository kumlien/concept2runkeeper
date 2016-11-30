package se.kumliens.concept2runkeeper.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created by svante on 2016-11-30.
 */
@RequiredArgsConstructor(access = PRIVATE)
public enum Authorities {

    USER("user"), ADMIN("admin");

    public final String role;

}
