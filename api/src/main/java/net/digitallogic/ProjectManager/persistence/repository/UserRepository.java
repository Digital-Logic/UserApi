package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity_;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.EntityGraphRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, UUID>,
		EntityGraphRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

	boolean existsByEmailIgnoreCase(String email);

	default Optional<UserEntity> findByEmail(String email) {
		return findOne(findByEmailSpec(email));
	}

	default Optional<UserEntity> findByEmail(String email, GraphResolver graphResolver) {
		return findOne(findByEmailSpec(email.toUpperCase()), graphResolver);
	}

	default Specification<UserEntity> findByEmailSpec(final String email) {
		return ((root, query, builder) ->
			builder.equal(
					builder.upper(root.get(UserEntity_.email)),
					email.toUpperCase()
			));
	}

	default Specification<UserEntity> filterByLastName(final String lastName) {
		return ((root, query, builder) ->
					builder.like(
						builder.upper(root.get(UserEntity_.lastName)),
							lastName.toUpperCase())
				);
	}

	default Specification<UserEntity> filterByFirstName(final String firstName) {
		return ((root, query, builder) ->
				builder.like(
						builder.upper(root.get(UserEntity_.firstName)),
								firstName.toUpperCase())
				);
	}

	default Specification<UserEntity> filterByAccountEnabled(LocalDateTime validAt) {
		return ((root, query, builder) -> {
			Join<UserEntity, UserStatusEntity> join = root.join(UserEntity_.userStatus, JoinType.LEFT);

			return builder.and(
					builder.lessThanOrEqualTo(join.get(UserStatusEntity_.id).get(BiTemporalEntityId_.validStart), validAt),
					builder.greaterThan(join.get(UserStatusEntity_.validStop), validAt),
					builder.lessThanOrEqualTo(join.get(UserStatusEntity_.id).get(BiTemporalEntityId_.systemStart), LocalDateTime.now(Clock.systemUTC())),
					builder.greaterThan(join.get(UserStatusEntity_.systemStop), LocalDateTime.now(Clock.systemUTC())),
					builder.equal(join.get(UserStatusEntity_.accountEnabled), true)
			);
		});
	}
}
