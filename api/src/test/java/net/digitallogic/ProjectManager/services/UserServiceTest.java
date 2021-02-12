package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.config.RepositoryConfig;
import net.digitallogic.ProjectManager.fixtures.RoleFixtures;
import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.repository.RoleRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import net.digitallogic.ProjectManager.web.error.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

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

	@Mock
	GraphBuilder<UserEntity> userGraphBuilder;

	@InjectMocks
	UserServiceImpl userService;

	@BeforeAll
	static void beforeAll() {
		RepositoryConfig.configUserEntityFilters();
	}

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

	@Test
	void getAllUsersTest() {
		when(userRepository.findAll(any(), any(PageRequest.class), any()))
				.thenReturn(new PageImpl<>(UserFixtures.userEntity(2), PageRequest.of(0, 2, Sort.by("createdDate")), 2));

		when(userGraphBuilder.createResolver(any())).thenReturn(null);

		Slice<UserDto> resultSet = userService.getAllUsers(0, 25, "createdDate", null, null);

		assertThat(resultSet).hasSize(2);
	}

	@Test
	void getUserTest() {
		when(userRepository.findById(any(UUID.class), any()))
				.thenReturn(Optional.of(UserFixtures.userEntity()));

		when(userGraphBuilder.createResolver(any())).thenReturn(null);

		UserDto result = userService.getUser(UUID.randomUUID(), null);
		assertThat(result).isNotNull();
	}

	@Test
	void getUserInvalidIdTest() {
		when(userRepository.findById(any(UUID.class), any()))
				.thenReturn(Optional.empty());

		when(userGraphBuilder.createResolver(any())).thenReturn(null);

		assertThatThrownBy(() ->
				userService.getUser(UUID.randomUUID(), null))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void updateUserTest() {
		UserEntity user = UserFixtures.userEntity();
		UserUpdateDto updateData = UserUpdateDto.builder()
				.id(user.getId())
				.lastName("SomethingNew")
				.build();

	}
}
