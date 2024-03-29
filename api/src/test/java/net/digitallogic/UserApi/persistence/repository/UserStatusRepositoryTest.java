package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.annotations.RepositoryTest;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.entity.user.UserStatusEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class UserStatusRepositoryTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	UserStatusRepository userStatusRepository;

	@Autowired
	UserRepository userRepository;

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void getCurrentEntityTest() {
		UserEntity user = userRepository
				.findByEmail("test@testing.com")
				.orElseThrow();

		Optional<UserStatusEntity> status =
				userStatusRepository.findByEntityId(user.getId(),
						LocalDateTime.now(Clock.systemUTC()));

		assertThat(status).isNotEmpty();
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void getHistoryOfEntityTest() {
		UserEntity user = userRepository.findByEmail("test@Testing.com").orElseThrow();

		Iterable<UserStatusEntity> status = userStatusRepository
				.getHistoryByEntityId(user.getId(), LocalDateTime.now(Clock.systemUTC()));

		assertThat(status).hasSize(2);
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void saveUserStatusTest() {
		LocalDateTime now = LocalDateTime.now();

		UserEntity user = userRepository.findByEmail("test@testing.com").orElseThrow();

		UserStatusEntity status = UserStatusEntity.builder()
				.accountLocked(true)
				.accountEnabled(true)
				.createdBy(user.getId())
				.user(user)
				.validStart(now)
				.systemStart(now)
				.build();

		assertThat(status.getEmbeddedId()).isNull();

		userStatusRepository.save(status);
		entityManager.flush();

		assertThat(status.getEmbeddedId()).isNotNull();
		System.out.println("UserID: " + status.getEmbeddedId());
	}
}
