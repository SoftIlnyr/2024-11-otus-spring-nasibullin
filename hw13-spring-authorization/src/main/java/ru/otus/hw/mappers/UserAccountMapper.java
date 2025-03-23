package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.models.UserAccount;
import ru.otus.hw.security.UserAccountDetails;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    @Mapping(source = "roles", target = "authorities")
    UserAccountDetails toUserAccount(UserAccount user);

}
