package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.hw.models.UserAccount;
import ru.otus.hw.repositories.UserAccountRepository;
import ru.otus.hw.security.UserRole;

@ChangeLog(order = "002")
public class UserInitChangelog {

    @ChangeSet(order = "001", id = "001_init_admin", author = "softi", runAlways = true)
    public void initUserAdmin(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("admin");
        userAccount.setPassword(passwordEncoder.encode("password")); //password
        userAccount.addRoles(UserRole.ADMIN);
        userAccountRepository.save(userAccount);
    }

    @ChangeSet(order = "002", id = "002_init_author", author = "softi", runAlways = true)
    public void initUserAuthor(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("author");
        userAccount.setPassword(passwordEncoder.encode("password")); //password
        userAccount.addRoles(UserRole.AUTHOR);
        userAccountRepository.save(userAccount);
    }

    @ChangeSet(order = "003", id = "003_init_author", author = "softi", runAlways = true)
    public void initUserReader(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("reader");
        userAccount.setPassword(passwordEncoder.encode("password")); //password
        userAccount.addRoles(UserRole.READER);
        userAccountRepository.save(userAccount);
    }

}
