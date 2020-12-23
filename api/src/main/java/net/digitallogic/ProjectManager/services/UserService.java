package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;

public interface UserService {
	UserDto createUser(CreateUserDto createUser);
}
