package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.annotations.RepositoryTest;
import net.digitallogic.UserApi.persistence.entity.auth.RoleEntity;
import net.digitallogic.UserApi.persistence.entity.auth.RoleEntity_;
import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.UserApi.security.ROLES;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class RoleRepositoryTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	GraphBuilder<RoleEntity> graphBuilder;

	private final PersistenceUtil pu = Persistence.getPersistenceUtil();

	@Test
	public void findByNameTest() {
		Optional<RoleEntity> role = roleRepository.findByName(ROLES.ADMIN.name);
		assertThat(role).isNotEmpty();
		assertThat(pu.isLoaded(role.get(), RoleEntity_.AUTHORITIES)).isFalse();
	}

	@Test
	public void findByNameEntityGraphTest() {

		Optional<RoleEntity> role = roleRepository.findByName(ROLES.ADMIN.name,
				graphBuilder.createResolver(RoleEntity_.AUTHORITIES));

		assertThat(role).isNotEmpty();
		assertThat(pu.isLoaded(role.get(), RoleEntity_.AUTHORITIES)).isTrue();
	}
}
