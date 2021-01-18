package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface EntityGraphRepository<T, ID extends Serializable> {
	Optional<T> findById(ID id, @Nullable GraphResolver graphResolver);
	Optional<T> findOne(@Nullable Specification<T> spec, @Nullable GraphResolver graphResolver);

	Iterable<T> findAll(@Nullable Specification<T> spec, @Nullable GraphResolver graphResolver);
	Iterable<T> findAll(@Nullable GraphResolver graphResolver);
	Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable, @Nullable GraphResolver graphResolver);
	Page<T> findAll(Pageable pageable, @Nullable GraphResolver graphResolver);
	List<T> findAll(Sort sort, @Nullable GraphResolver graphResolver);
}
