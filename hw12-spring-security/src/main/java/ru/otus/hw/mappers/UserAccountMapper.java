package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import ru.otus.hw.models.UserAccount;
import ru.otus.hw.security.UserAccountDetails;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccountDetails toUserAccount(UserAccount user);
}
