package net.digitallogic.UserApi.persistence.repositoryFactory;

import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface EntityGraphRepository<T, ID extends Serializable> {
	Optional<T> findById(ID id, @Nullable GraphResolver<T> graphResolver);
	Optional<T> findOne(@Nullable Specification<T> spec, @Nullable GraphResolver<T> graphResolver);

	Iterable<T> findAll(@Nullable Specification<T> spec, @Nullable GraphResolver<T> graphResolver);
	Iterable<T> findAll(@Nullable GraphResolver<T> graphResolver);
	Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable, @Nullable GraphResolver<T> graphResolver);
	Page<T> findAll(Pageable pageable, @Nullable GraphResolver<T> graphResolver);
	List<T> findAll(Sort sort, @Nullable GraphResolver<T> graphResolver);
}
