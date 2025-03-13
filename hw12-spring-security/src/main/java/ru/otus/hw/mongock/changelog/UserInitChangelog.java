package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.hw.models.UserAccount;
import ru.otus.hw.repositories.UserAccountRepository;

@ChangeLog(order = "002")
public class UserInitChangelog {

    @ChangeSet(order = "001", id = "001_init_user", author = "softi", runAlways = true)
    public void initUsers(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("user");
        userAccount.setPassword(passwordEncoder.encode("password")); //password
        userAccountRepository.save(userAccount);
    }

}
