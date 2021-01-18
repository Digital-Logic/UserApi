package net.digitallogic.ProjectManager.config;

import net.digitallogic.ProjectManager.persistence.entity.user.*;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.AdvancedJpaRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.RepositoryFactoryBean;
import net.digitallogic.ProjectManager.web.filter.SpecSupport;
import net.digitallogic.ProjectManager.web.filter.operators.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;

@Configuration
@ComponentScan(value = "net.digitallogic.ProjectManager.persistence")
@EnableJpaRepositories(
		basePackages = "net.digitallogic.ProjectManager.persistence",
		repositoryFactoryBeanClass = RepositoryFactoryBean.class,
		excludeFilters = {
				@Filter(type = FilterType.ASSIGNABLE_TYPE, value = AdvancedJpaRepository.class)
		}
)
public class RepositoryConfig {

	static {
		configUserEntityFilters();
	}

	@Bean
	public GraphBuilder<UserEntity> userEntityGraphBuilder() {
		return GraphBuilder.builder(UserEntity.class)
				.addProperty(UserEntity_.ROLES)
				.addProperty(RoleEntity_.AUTHORITIES,
						g -> g.addSubgraph(UserEntity_.roles)
							.addSubgraph(RoleEntity_.AUTHORITIES))
				.build();
	}

	@Bean
	public GraphBuilder<RoleEntity> roleEntityGraphBuilder() {
		return GraphBuilder.builder(RoleEntity.class)
				.addProperty(RoleEntity_.AUTHORITIES)
				.build();
	}

	@Bean
	public GraphBuilder<UserStatusEntity> userStatusGraphBuilder() {
		return GraphBuilder.builder(UserStatusEntity.class)
				.addProperty(UserStatusEntity_.USER)
				.addProperty(UserStatusEntity_.AUDIT_MESSAGE)
				.build();
	}


	public static void configUserEntityFilters() {
		SpecSupport.addFilter(UserEntity.class)
				.addProperty(UserEntity_.FIRST_NAME, String.class)
					.addComparator(new Equals<>())
					.addComparator(new Like<>())
					.addComparator(new Ilike<>())
					.build()
				.addProperty(UserEntity_.LAST_NAME, String.class)
					.addComparator(new Equals<>())
					.addComparator(new Like<>())
					.addComparator(new Ilike<>())
					.build()
				.addProperty(UserEntity_.CREATED_DATE, LocalDateTime.class)
					.addComparator(new Equals<>())
					.addComparator(new LessThan<>())
					.addComparator(new GreaterThan<>())
					.addComparator(new LessThanOrEqual<>())
					.addComparator(new GreaterThanOrEqual<>())
					.build()
				.addProperty(UserStatusEntity_.ACCOUNT_ENABLED, LocalDateTime.class)
					.resolvePath(false)
					.addComparator(new AccountEnabled())
					.build()
				.addProperty(UserEntity_.ARCHIVED, Boolean.class)
					.addComparator(new Equals<>())
					.build()
				.build();
	}
}














