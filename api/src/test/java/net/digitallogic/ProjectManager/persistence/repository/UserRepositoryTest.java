package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.config.RepositoryConfig;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity_;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.ROLES;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

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
		assertThat(userEntity.get().getId()).isNotNull();

		Optional<UserEntity> user = userRepository.findById(userEntity.get().getId());
		assertThat(user).isNotEmpty();
	}

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	public void userEntityGraphRolesTest() {

		GraphBuilder<UserEntity> graphBuilder = new RepositoryConfig().userEntityGraphBuilder();

		PersistenceUtil pu = Persistence.getPersistenceUtil();

		Optional<UserEntity> user = userRepository
				.findByEmail(
						"adminTestUser@gmail.com", graphBuilder.createResolver("roles")
				);

		assertThat(user).isNotEmpty();
		assertThat(pu.isLoaded(user.get(), UserEntity_.ROLES)).isTrue();

		assertThat(user.get().getRoles()).hasSize(1);
		// Verify that authorities have not been loaded
		user.get().getRoles().forEach(role ->
				assertThat(pu.isLoaded(role, RoleEntity_.AUTHORITIES)).isFalse());
	}

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	public void userEntityGraphAuthoritiesTest() {
		GraphBuilder<UserEntity> graphBuilder = new RepositoryConfig().userEntityGraphBuilder();

		PersistenceUtil pu = Persistence.getPersistenceUtil();

		Optional<UserEntity> user = userRepository.findByEmail(
				"adminTestUser@gmail.com", graphBuilder.createResolver("authorities")
		);

		assertThat(user).isNotEmpty();

		assertThat(pu.isLoaded(user.get(), UserEntity_.ROLES)).isTrue();
		assertThat(user.get().getRoles()).hasSize(1);

		user.get().getRoles().forEach(role ->
				assertThat(pu.isLoaded(role, RoleEntity_.AUTHORITIES)));

		user.get().getRoles().forEach(role -> {
			assertThat(role.getName()).isEqualTo(ROLES.ADMIN.name);
			assertThat(role.getAuthorities()).hasSize(2);
		});
	}

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	public void getAllUserTest() {
		Slice<UserEntity> userSlice = userRepository.findAll(
				PageRequest.of(0, 25)
		);

		assertThat(userSlice).hasSize(2);
	}

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	public void getAllUsersGraphTest() {
		GraphBuilder<UserEntity> userGraphBuilder = new RepositoryConfig().userEntityGraphBuilder();
		final PersistenceUtil pu = Persistence.getPersistenceUtil();

		Stream.of("  ", UserEntity_.ROLES, RoleEntity_.AUTHORITIES, UserEntity_.ROLES + "," + RoleEntity_.AUTHORITIES)
				.forEach(graphNodes -> {

					Slice<UserEntity> userSlice = userRepository.findAll(
							PageRequest.of(0, 25, Sort.by("email")),
							userGraphBuilder.createResolver(graphNodes)
					);
					if (graphNodes.equals(UserEntity_.ROLES)) {
						userSlice.forEach(user ->
								assertThat(pu.isLoaded(user, UserEntity_.ROLES)));
					} else if (graphNodes.equals(RoleEntity_.AUTHORITIES)) {
						userSlice.forEach(user -> {
							assertThat(pu.isLoaded(user, UserEntity_.ROLES));
							user.getRoles().forEach(role ->
									assertThat(pu.isLoaded(role, RoleEntity_.AUTHORITIES)));
						});
					}
					assertThat(userSlice).hasSize(2);
				});
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void filterByAccountEnabledTest2() {
		Optional<UserEntity> user = userRepository.findOne(
				userRepository.findByEmailSpec("test@testing.com")
						.and(userRepository
								.filterByAccountEnabled(
										LocalDateTime.now(Clock.systemUTC())
										.minusDays(26)
								)
						)
		);

		assertThat(user).isEmpty();
	}
}














