package net.digitallogic.ProjectManager.web.controller;

import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.services.UserService;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

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
	public UserDto createUserAccount(@RequestBody @Valid CreateUserDto createUser) {
		// TODO change return type to void
		return userService.createUser(createUser);
	}

	@Override
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<UserDto> getAllUsers(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "limit", defaultValue = "25") int limit,
			@RequestParam(name = "sort", defaultValue = "createdDate") String sort,
			@RequestParam(name = "filter", required = false) @Nullable String filter,
			@RequestParam(name = "expand", required = false) @Nullable String expand,
			HttpServletResponse response) {

		Slice<UserDto> dtoSlice = userService.getAllUsers(page, limit, sort, filter, expand);

		response.addHeader("Pagination-Limit", Integer.toString(limit));
		response.addHeader("Pagination-Page", Integer.toString(page));
		response.addHeader("Pagination-HasNext", Boolean.toString(dtoSlice.hasNext()));

		return dtoSlice.toList();
	}

	@Override
	@GetMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto getUser(@PathVariable UUID id,
	                       @RequestParam(name = "expand", required = false) @Nullable String expand) {
		return userService.getUser(id, expand);
	}

	@Override
	@PutMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
		return userService.updateUser(id, userUpdateDto);
	}
}
