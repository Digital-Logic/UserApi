package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

public interface UserService {
	UserDto createUser(CreateUserDto createUser);
	Slice<UserDto> getAllUsers(int page, int limit,
	                           @Nullable String sort, @Nullable String filter, @Nullable String expand);
}
