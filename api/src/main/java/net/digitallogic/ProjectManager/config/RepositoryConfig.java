package net.digitallogic.ProjectManager.config;

import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity_;
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

	private EntityManager entityManager;

	@Autowired
	public RepositoryConfig(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Bean
	public EntityGraphBuilder<UserEntity> userEntityGraphBuilder() {
		return new EntityGraphBuilder<UserEntity>(
				entityManager,
				UserEntity.class,
				Map.ofEntries(
						entry("roles", graph ->
								graph.addSubgraph(UserEntity_.roles)),
						entry("authorities", graph -> {
							graph.addSubgraph(UserEntity_.roles)
									.addSubgraph(RoleEntity_.AUTHORITIES);
						})
				)
		);
	}

}
