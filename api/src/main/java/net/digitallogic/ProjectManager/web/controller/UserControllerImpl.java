package net.digitallogic.ProjectManager.web.controller;

import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.services.UserService;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = Routes.USER_ROUTE)
public class UserControllerImpl implements UserController {

	private final UserService userService;

	@Autowired
	public UserControllerImpl(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto createUserAccount(@Valid @RequestBody CreateUserDto createUser) {
		// TODO change return type to void

		return userService.createUser(createUser);
	}
}
