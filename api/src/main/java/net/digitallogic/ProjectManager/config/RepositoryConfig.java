package net.digitallogic.ProjectManager.config;

import net.digitallogic.ProjectManager.persistence.repositoryFactory.AdvancedJpaRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.RepositoryFactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
}
