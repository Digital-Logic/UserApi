package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.fixtures.RoleFixtures;
import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.repository.RoleRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.web.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	RoleRepository roleRepository;

	@Mock
	UserStatusRepository userStatusRepository;

	@Mock
	PasswordEncoder encoder;

	@Mock
	MessageSource messageSource;

	@InjectMocks
	UserServiceImpl userService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void createUserTest() {
		CreateUserDto createUserDto = UserFixtures.createUser();

		when(userRepository.existsByEmailIgnoreCase(anyString()))
				.thenReturn(false);

		when(roleRepository.findByName(anyString()))
				.thenReturn(Optional.of(RoleFixtures.roleEntity()));

		when(encoder.encode(anyString()))
				.thenReturn("encodedPassword");

		when(userRepository.save(any(UserEntity.class)))
				.then(invocation -> invocation.getArgument(0));

		when(userRepository.findByEmail(anyString()))
				.thenReturn(Optional.of(UserFixtures.userEntity()));

		when(userStatusRepository.save(any(UserStatusEntity.class)))
				.then(invocation -> invocation.getArgument(0));


		UserDto response = userService.createUser(createUserDto);

		verify(encoder, times(1)).encode(anyString());

		assertThat(response).as("UserService response is null").isNotNull();
		assertThat(response).isEqualToComparingOnlyGivenFields(createUserDto,
				"email", "firstName", "lastName");
		assertThat(response.getId()).isNotNull();
	}

	@Test
	void createDuplicateUserTest() {
		when(userRepository.existsByEmailIgnoreCase(anyString()))
				.thenReturn(true);

		assertThatThrownBy(() ->
				userService.createUser(UserFixtures.createUser()))
				.isInstanceOf(BadRequestException.class);
	}
}
