package net.digitallogic.UserApi.services;

import com.github.javafaker.Faker;
import net.digitallogic.UserApi.config.Profiles;
import net.digitallogic.UserApi.fixtures.UserFixtures;
import net.digitallogic.UserApi.persistence.dto.user.CreateUserRequest;
import net.digitallogic.UserApi.persistence.dto.user.UserDto;
import net.digitallogic.UserApi.persistence.dto.user.UserUpdateDto;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles(Profiles.NON_ASYNC)
public class UserServiceIntegrationTest {

	private final Faker faker = new Faker();

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;


	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void getUserTest() {

		UserEntity userEntity = userRepository.findByEmail("joe@exotic.net")
				.orElseThrow();

		UserDto user = userService.getUser(userEntity.getId(), null);

		assertThat(user).isNotNull();
		assertThat(user).isEqualToComparingOnlyGivenFields(userEntity,
				"id", "firstName", "lastName", "email");
	}

	@Test
	public void createUserTest() {
		UserDto newUser = UserFixtures.userDto();
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.email(newUser.getEmail())
				.lastName(newUser.getLastName())
				.firstName(newUser.getFirstName())
				.password("Password")
				.build();

		UserDto response = userService.createUser(createUserRequest);

		assertThat(response).isNotNull();
		assertThat(response).isEqualToComparingOnlyGivenFields(newUser,
				"email", "firstName", "lastName");

		UserEntity persisted = userRepository.findById(response.getId())
				.orElseThrow();

		assertThat(response).isEqualToComparingOnlyGivenFields(persisted,
				"id", "email", "firstName", "lastName");
	}


	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void updateUserTest() {
		UserEntity userEntity = userRepository.findByEmail("joe@Exotic.net")
				.orElseThrow();

		UserDto user = userService.updateUser(
				userEntity.getId(),
				UserUpdateDto.builder()
						.id(userEntity.getId())
						.lastName("Dirt")
						.version(userEntity.getVersion())
						.build());

		assertThat(user).isNotNull();
		assertThat(user).isEqualToComparingOnlyGivenFields(userEntity,
				"id", "firstName", "email", "createdDate");
		assertThat(user.getLastName()).isEqualToIgnoringCase("Dirt");
		//assertThat(user.getLastModifiedDate()).isCloseToUtcNow(within(5, ChronoUnit.MINUTES));
	}
}
