package net.digitallogic.ProjectManager.web.controller;

import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserController {
	UserDto createUserAccount(CreateUserDto createUser);
	List<UserDto> getAllUsers(int page, int limit, String sort, String expand, String filter, HttpServletResponse response);
}
