package net.digitallogic.ProjectManager.annotations;

import net.digitallogic.ProjectManager.config.RepositoryConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@EnableJpaRepositories(
//		basePackages = "net.digitallogic.ProjectManager.persistence",
//		repositoryFactoryBeanClass = RepositoryFactoryBean.class,
//		excludeFilters = {
//				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AdvancedJpaRepository.class)
//		}
//)
@Import(RepositoryConfig.class)
public @interface RepositoryTest {
}
