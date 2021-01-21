package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

import java.util.UUID;

public interface UserService {
	UserDto createUser(CreateUserDto createUser);
	UserDto getUser(UUID id, @Nullable String expand);
	Slice<UserDto> getAllUsers(int page, int limit,
	                           @Nullable String sort,
	                           @Nullable String filter,
	                           @Nullable String expand);

	UserDto updateUser(UUID id, UserUpdateDto updateUser);
}
