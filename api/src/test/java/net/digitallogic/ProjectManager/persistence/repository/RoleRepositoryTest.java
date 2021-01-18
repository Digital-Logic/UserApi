package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.config.RepositoryConfig;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.ROLES;
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
	EntityManager entityManager;

	@Autowired
	RoleRepository roleRepository;

	PersistenceUtil pu = Persistence.getPersistenceUtil();

	@Test
	public void findByNameTest() {
		Optional<RoleEntity> role = roleRepository.findByName(ROLES.ADMIN.name);
		assertThat(role).isNotEmpty();
		assertThat(pu.isLoaded(role.get(), RoleEntity_.AUTHORITIES)).isFalse();
	}

	@Test
	public void findByNameEntityGraphTest() {
		GraphBuilder<RoleEntity> graphBuilder = new RepositoryConfig().roleEntityGraphBuilder();

		Optional<RoleEntity> role = roleRepository.findByName(ROLES.ADMIN.name,
				graphBuilder.createResolver(RoleEntity_.AUTHORITIES));

		assertThat(role).isNotEmpty();
		assertThat(pu.isLoaded(role.get(), RoleEntity_.AUTHORITIES)).isTrue();
	}
}
