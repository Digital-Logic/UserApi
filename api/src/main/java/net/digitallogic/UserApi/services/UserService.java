package net.digitallogic.UserApi.services;

import net.digitallogic.UserApi.persistence.dto.user.CreateUserRequest;
import net.digitallogic.UserApi.persistence.dto.user.UserUpdateDto;
import net.digitallogic.UserApi.persistence.dto.user.UserDto;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

import java.util.UUID;

public interface UserService {
	UserDto createUser(CreateUserRequest createUser);
	UserDto getUser(UUID id, @Nullable String expand);
	Slice<UserDto> getAllUsers(int page, int limit,
	                           @Nullable String sort,
	                           @Nullable String filter,
	                           @Nullable String expand);

	UserDto updateUser(UUID id, UserUpdateDto updateUser);
}
