package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.persistence.entity.auth.RoleEntity;
import net.digitallogic.UserApi.persistence.entity.auth.RoleEntity_;
import net.digitallogic.UserApi.persistence.repositoryFactory.EntityGraphRepository;
import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends CrudRepository<RoleEntity, UUID>,
		EntityGraphRepository<RoleEntity, UUID>, JpaSpecificationExecutor<RoleEntity> {

	default Optional<RoleEntity> findByName(String name) {
		return findOne(findByNameSpec(name));
	}

	default Optional<RoleEntity> findByName(String name, GraphResolver<RoleEntity> graphResolver) {
		return findOne(findByNameSpec(name), graphResolver);
	}

	default Specification<RoleEntity> findByNameSpec(final String name) {
		return ((root, query, builder) ->
			builder.equal(
					builder.upper(root.get(RoleEntity_.name)),
					name.toUpperCase()
			)
		);
	}
}
