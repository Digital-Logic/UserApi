package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
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
		UserEntity user = userRepository.findByEmail("test@testing.com").orElseThrow();

		UserStatusEntity status = UserStatusEntity.builder()
				.accountLocked(true)
				.accountEnabled(true)
				.createdBy(user.getId())
				.user(user)
				.build();

		assertThat(status.getEmbeddedId()).isNull();

		userStatusRepository.save(status);
		entityManager.flush();

		assertThat(status.getEmbeddedId()).isNotNull();
		System.out.println("UserID: " + status.getEmbeddedId());
	}
}
