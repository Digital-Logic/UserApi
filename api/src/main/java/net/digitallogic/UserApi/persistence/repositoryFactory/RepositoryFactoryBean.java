package net.digitallogic.UserApi.persistence.repositoryFactory;

import net.digitallogic.UserApi.persistence.biTemporal.repository.BiTemporalRepository;
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

	private final Class<? extends R> repositoryInterface;

	public RepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
		this.repositoryInterface = repositoryInterface;
	}

	/**
	 * Creates and returns a new repository factory.
	 * @param entityManager
	 * @return
	 */
	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

		if (BiTemporalRepository.class.isAssignableFrom(repositoryInterface)) {
			// return BiTemporal Repository
			BiTemporalRepositoryFactory factory = new BiTemporalRepositoryFactory(entityManager);
			factory.setEntityPathResolver(SimpleEntityPathResolver.INSTANCE);
			factory.setEscapeCharacter(EscapeCharacter.DEFAULT);

			return factory;

		} else {
			RepositoryFactory factory = new RepositoryFactory(entityManager);
			factory.setEntityPathResolver(SimpleEntityPathResolver.INSTANCE);
			factory.setEscapeCharacter(EscapeCharacter.DEFAULT);

			return factory;
		}
	}

	private static class RepositoryFactory extends JpaRepositoryFactory {

		public RepositoryFactory(EntityManager entityManager) {
			super(entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return AdvancedJpaRepository.class;
		}
	}

	private static class BiTemporalRepositoryFactory extends JpaRepositoryFactory {

		public BiTemporalRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return BiTemporalJpaRepository.class;
		}
	}
}
