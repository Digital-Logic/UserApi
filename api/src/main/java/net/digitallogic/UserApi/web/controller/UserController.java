package net.digitallogic.UserApi.web.controller;

import net.digitallogic.UserApi.persistence.dto.user.CreateUserRequest;
import net.digitallogic.UserApi.persistence.dto.user.UserUpdateDto;
import net.digitallogic.UserApi.persistence.dto.user.UserDto;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

public interface UserController {
	UserDto createUserAccount(CreateUserRequest createUser);

	UserDto getUser(UUID id, @Nullable String expand);

	List<UserDto> getAllUsers(int page, int limit,
	                          String sort,
	                          @Nullable String expand,
	                          @Nullable String filter,
	                          HttpServletResponse response);

	UserDto updateUser(UUID id, UserUpdateDto userUpdateDto);
}
