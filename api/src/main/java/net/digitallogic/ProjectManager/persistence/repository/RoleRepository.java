package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.EntityGraphRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityGraph;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends CrudRepository<RoleEntity, UUID>,
		EntityGraphRepository<RoleEntity, UUID>, JpaSpecificationExecutor<RoleEntity> {

	default Optional<RoleEntity> findByName(String name) {
		return findOne(findByNameSpec(name));
	}

	default Optional<RoleEntity> findByName(String name, EntityGraph<RoleEntity> graph) {
		return findOne(findByNameSpec(name), graph);
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
