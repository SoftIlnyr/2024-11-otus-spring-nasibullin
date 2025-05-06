package ru.otus.hw.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

@Getter
public enum UserRole implements GrantedAuthority {

    ADMIN("ROLE_ADMIN"),
    AUTHOR("ROLE_AUTHOR"),
    READER("ROLE_READER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public static UserRole getByAuthority(String authority) {
        return Arrays.stream(UserRole.values())
                .filter(userRole -> userRole.authority.equals(authority))
                .findFirst()
                .orElse(null);
    }

}
