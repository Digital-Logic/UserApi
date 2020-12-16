package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.config.RepositoryConfig;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity_;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.EntityGraphBuilder;
import net.digitallogic.ProjectManager.security.ROLES;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class UserRepositoryTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	UserRepository userRepository;

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void existByEmailTest() {
		assertThat(userRepository.existsByEmailIgnoreCase("Test@Testing.com"))
				.isTrue();
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void findByEmailIgnoreCaseTest() {
		Optional<UserEntity> user = userRepository.findByEmail("Test@Testing.com");
		assertThat(user).isNotEmpty();
		assertThat(user.get().getEmail()).isEqualToIgnoringCase("test@testing.com");
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void findByIdTest() {
		Optional<UserEntity> userEntity = userRepository.findByEmail("Test@Testing.com");
		assertThat(userEntity).isNotEmpty();

		Optional<UserEntity> user = userRepository.findById(userEntity.get().getId());
		assertThat(user).isNotEmpty();
	}

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	public void userEntityGraphRolesTest() {

		EntityGraphBuilder<UserEntity> entityGraphBuilder = new RepositoryConfig(entityManager)
				.userEntityGraphBuilder();

		PersistenceUtil pu = Persistence.getPersistenceUtil();

		Optional<UserEntity> user = userRepository
				.findByEmail(
						"adminTestUser@gmail.com", entityGraphBuilder.createEntityGraph("roles")
				);

		assertThat(user).isNotEmpty();
		assertThat(pu.isLoaded(user.get(), UserEntity_.ROLES)).isTrue();

		assertThat(user.get().getRoles()).hasSize(1);
		// Verify that authorities have not been loaded
		user.get().getRoles().forEach(role -> {
			assertThat(pu.isLoaded(role, RoleEntity_.AUTHORITIES)).isFalse();
		});
	}

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	public void userEntityGraphAuthoritiesTest() {
		EntityGraphBuilder<UserEntity> graphBuilder = new RepositoryConfig(entityManager)
				.userEntityGraphBuilder();

		PersistenceUtil pu = Persistence.getPersistenceUtil();

		Optional<UserEntity> user = userRepository.findByEmail(
				"adminTestUser@gmail.com", graphBuilder.createEntityGraph("authorities")
		);

		assertThat(user).isNotEmpty();

		assertThat(pu.isLoaded(user.get(), UserEntity_.ROLES)).isTrue();
		assertThat(user.get().getRoles()).hasSize(1);

		user.get().getRoles().forEach(role -> {
			assertThat(pu.isLoaded(role, RoleEntity_.AUTHORITIES));
		});

		user.get().getRoles().forEach(role -> {
			assertThat(role.getName()).isEqualTo(ROLES.ADMIN.name);
			assertThat(role.getAuthorities()).hasSize(2);
		});
	}
}














