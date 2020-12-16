package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class UserEntityRepositoryTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	UserEntityRepository userEntityRepository;


	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void existByEmailTest() {
		assertThat(userEntityRepository.existsByEmailIgnoreCase("Test@Testing.com"))
				.isTrue();
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void findByEmailIgnoreCaseTest() {
		Optional<UserEntity> user = userEntityRepository.findByEmailIgnoreCase("Test@Testing.com");
		assertThat(user).isNotEmpty();
		assertThat(user.get().getEmail()).isEqualToIgnoringCase("test@testing.com");
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void findByIdTest() {
		Optional<UserEntity> userEntity = userEntityRepository.findByEmailIgnoreCase("Test@Testing.com");
		assertThat(userEntity).isNotEmpty();

		Optional<UserEntity> user = userEntityRepository.findById(userEntity.get().getId());
		assertThat(user).isNotEmpty();
	}
}
