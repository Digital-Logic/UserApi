package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class RepositoryFactoryBean<R extends Repository<T, ID>, T, ID extends Serializable>
	extends JpaRepositoryFactoryBean<R, T, ID> {

	public RepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		RepositoryFactory<T, ID> factory = new RepositoryFactory<>(entityManager);
		factory.setEntityPathResolver(SimpleEntityPathResolver.INSTANCE);
		factory.setEscapeCharacter(EscapeCharacter.DEFAULT);

		return factory;
	}

	private static class RepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {
		private final EntityManager entityManager;

		public RepositoryFactory(EntityManager entityManager) {
			super(entityManager);
			this.entityManager = entityManager;
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return AdvancedJpaRepository.class;
		}
	}
}
