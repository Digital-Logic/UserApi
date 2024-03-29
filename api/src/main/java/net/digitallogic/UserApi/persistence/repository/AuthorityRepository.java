package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.persistence.entity.auth.AuthorityEntity;
import net.digitallogic.UserApi.persistence.entity.auth.AuthorityEntity_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorityRepository extends CrudRepository<AuthorityEntity, UUID>,
		JpaSpecificationExecutor<AuthorityEntity> {

	default Optional<AuthorityEntity> findByName(String name) {
		return findOne(findByNameSpec(name));
	}

	default Specification<AuthorityEntity> findByNameSpec(final String name) {
		return ((root, query, builder) ->
				builder.equal(builder.upper(root.get(AuthorityEntity_.name)), name.toUpperCase()));
	}
}
