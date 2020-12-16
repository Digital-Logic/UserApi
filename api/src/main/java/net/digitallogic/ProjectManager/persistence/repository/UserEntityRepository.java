package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity_;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.EntityGraphRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.EntityGraph;
import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends PagingAndSortingRepository<UserEntity, UUID>,
		EntityGraphRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

	boolean existsByEmailIgnoreCase(String email);

	Optional<UserEntity> findByEmail(String email);

	default Optional<UserEntity> findByEmail(String email, EntityGraph<UserEntity> graph) {
		return findOne(findByEmailSpec(email.toUpperCase()), graph);
	}

	default Specification<UserEntity> findByEmailSpec(final String email) {
		return ((root, query, builder) ->
			builder.equal(
					builder.upper(root.get(UserEntity_.email)),
					email.toUpperCase()
			));
	}
}
