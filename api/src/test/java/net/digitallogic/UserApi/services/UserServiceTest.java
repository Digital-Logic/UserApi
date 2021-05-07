package net.digitallogic.UserApi.services;

import net.digitallogic.UserApi.config.RepositoryConfig;
import net.digitallogic.UserApi.events.AccountRegistrationCompletedEvent;
import net.digitallogic.UserApi.fixtures.RoleFixtures;
import net.digitallogic.UserApi.fixtures.UserFixtures;
import net.digitallogic.UserApi.persistence.dto.user.CreateUserRequest;
import net.digitallogic.UserApi.persistence.dto.user.UserDto;
import net.digitallogic.UserApi.persistence.dto.user.UserUpdateDto;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.entity.user.UserStatusEntity;
import net.digitallogic.UserApi.persistence.repository.RoleRepository;
import net.digitallogic.UserApi.persistence.repository.UserRepository;
import net.digitallogic.UserApi.persistence.repository.UserStatusRepository;
import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.UserApi.web.error.exceptions.BadRequestException;
import net.digitallogic.UserApi.web.error.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Clock;
import java.time.ZoneId;
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
	ApplicationEventPublisher eventPublisher;

	@Mock
	PasswordEncoder encoder;

	@Mock
	GraphBuilder<UserEntity> userGraphBuilder;


	Clock systemClock = Clock.fixed(Clock.systemDefaultZone()
			.instant(), ZoneId.of("UTC"));

	UserServiceImpl userService;

	AutoCloseable closeable;

	@BeforeAll
	static void beforeAll() {
		RepositoryConfig.configUserEntityFilters();
	}

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);

		userService = new UserServiceImpl(userRepository,
				userGraphBuilder,
				userStatusRepository,
				eventPublisher,
				roleRepository,
				encoder,
				systemClock);
	}

	@AfterEach
	void teardown() throws Exception {
		closeable.close();
	}

	@Test
	void createUserTest() {
		CreateUserRequest createUserRequest = UserFixtures.createUser();

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

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		doNothing().when(eventPublisher).publishEvent(AccountRegistrationCompletedEvent.class);

		UserDto response = userService.createUser(createUserRequest);

		verify(encoder, times(1)).encode(anyString());
		verify(eventPublisher, times(1)).publishEvent(any(AccountRegistrationCompletedEvent.class));

		assertThat(response).as("UserService response is null").isNotNull();
		assertThat(response).isEqualToComparingOnlyGivenFields(createUserRequest,
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

		when(userGraphBuilder.createResolver(anyString())).thenReturn(null);

		Slice<UserDto> resultSet = userService.getAllUsers(0, 25, "createdDate", null, null);

		assertThat(resultSet).hasSize(2);
	}

	@Test
	void getUserTest() {
		when(userRepository.findById(any(UUID.class), any()))
				.thenReturn(Optional.of(UserFixtures.userEntity()));

		when(userGraphBuilder.createResolver(anyString())).thenReturn(null);

		UserDto result = userService.getUser(UUID.randomUUID(), null);
		assertThat(result).isNotNull();
	}

	@Test
	void getUserInvalidIdTest() {
		when(userRepository.findById(any(UUID.class), any()))
				.thenReturn(Optional.empty());

		when(userGraphBuilder.createResolver(anyString())).thenReturn(null);

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
