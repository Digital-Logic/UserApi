package net.digitallogic.ProjectManager.services;

import com.github.javafaker.Faker;
import net.digitallogic.ProjectManager.config.Profiles;
import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserRequest;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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

	@Test
	@Disabled // Requires manual database setup
	public void optimisticLockExceptionTest() throws InterruptedException {
		UserEntity userEntity = userRepository.findByEmail("joe@Exotic.net")
				.orElseThrow();

		final int max = 6;
		CountDownLatch latch = new CountDownLatch(1);
		CyclicBarrier barrier = new CyclicBarrier(3);

		Stream.generate(faker.name()::lastName)
				.limit(max)
				.map(name -> UserUpdateDto.builder()
						.id(userEntity.getId())
						.lastName(name)
						.version(userEntity.getVersion())
					.build())
				.map(update -> (Runnable)() -> {
					try {
						barrier.await();
						UserDto response = userService.updateUser(userEntity.getId(), update);
					} catch (OptimisticLockException ex) {
						System.out.println("OptimisticLock: " + ex.getEntity() + ", message: " + ex.getMessage());
						latch.countDown();
					} catch (ObjectOptimisticLockingFailureException ex) {
						System.out.println("ObjectOptimisticLock: " + ex.getMessage() + ", " + ex.getPersistentClass());
						latch.countDown();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				})
				.map(Thread::new)
				.forEach(Thread::start);

		latch.await(5000, TimeUnit.MILLISECONDS);
		System.out.println("Test completed!");
	}
}
