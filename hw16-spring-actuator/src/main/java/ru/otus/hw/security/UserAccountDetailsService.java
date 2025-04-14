package ru.otus.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.mappers.UserAccountMapper;
import ru.otus.hw.models.UserAccount;
import ru.otus.hw.repositories.UserAccountRepository;

@Service
@RequiredArgsConstructor
public class UserAccountDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    private final UserAccountMapper userAccountMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return userAccountMapper.toUserAccount(userAccount);
    }
}
