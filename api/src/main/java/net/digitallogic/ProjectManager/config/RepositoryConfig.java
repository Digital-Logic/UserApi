package net.digitallogic.ProjectManager.config;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity_;
import net.digitallogic.ProjectManager.persistence.entity.user.*;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.AdvancedJpaRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.EntityGraphBuilder;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.RepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;
import java.util.Map;

import static java.util.Map.entry;

@Configuration
@ComponentScan(value = "net.digitallogic.ProjectManager")
@EnableJpaRepositories(
		basePackages = "net.digitallogic.ProjectManager.persistence",
		repositoryFactoryBeanClass = RepositoryFactoryBean.class,
		excludeFilters = {
				@Filter(type = FilterType.ASSIGNABLE_TYPE, value = AdvancedJpaRepository.class)
		}
)
public class RepositoryConfig {

	private final EntityManager entityManager;

	@Autowired
	public RepositoryConfig(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Bean
	public EntityGraphBuilder<UserEntity> userEntityGraphBuilder() {
		return new EntityGraphBuilder<>(
				entityManager,
				UserEntity.class,
				Map.ofEntries(
						entry(UserEntity_.ROLES, graph ->
								graph.addSubgraph(UserEntity_.roles)),
						entry(RoleEntity_.AUTHORITIES, graph ->
							graph.addSubgraph(UserEntity_.roles)
									.addSubgraph(RoleEntity_.AUTHORITIES)
						)
				)
		);
	}

	@Bean
	public EntityGraphBuilder<RoleEntity> roleEntityGraphBuilder() {
		return new EntityGraphBuilder<>(
				entityManager,
				RoleEntity.class,
				Map.ofEntries(
						entry(RoleEntity_.AUTHORITIES,
								graph -> graph.addSubgraph(RoleEntity_.authorities))
				)
		);
	}

	@Bean
	public EntityGraphBuilder<UserStatusEntity> userStatusGraphBuilder() {
		return new EntityGraphBuilder<>(
				entityManager,
				UserStatusEntity.class,
				Map.ofEntries(
					entry(UserStatusEntity_.USER,
							graph -> graph.addSubgraph(UserStatusEntity_.user)),
					entry(BiTemporalEntity_.AUDIT_MESSAGE,
							graph -> graph.addSubgraph(UserStatusEntity_.AUDIT_MESSAGE))
				)
		);
	}
}














