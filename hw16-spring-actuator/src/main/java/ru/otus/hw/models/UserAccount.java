package ru.otus.hw.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.hw.security.UserRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class UserAccount {
    @Id
    private String id;

    private String username;

    private String password;

    private List<UserRole> roles;

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>();
        this.roles.add(UserRole.READER);
    }

    public UserAccount(String username, String password, List<UserRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public void addRoles(UserRole... userRoles) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        this.roles.addAll(Arrays.asList(userRoles));
    }


}
