package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.persistence.EntityGraph;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface EntityGraphRepository<T, ID extends Serializable> {
	Optional<T> findById(ID id, @Nullable EntityGraph<T> graph);
	Optional<T> findOne(@Nullable Specification<T> spec, @Nullable EntityGraph<T> graph);

	Iterable<T> findAll(@Nullable Specification<T> spec, @Nullable EntityGraph<T> graph);
	Iterable<T> findAll(@Nullable EntityGraph<T> graph);
	Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable, @Nullable EntityGraph<T> graph);
	Page<T> findAll(Pageable pageable, @Nullable EntityGraph<T> graph);
	List<T> findAll(Sort sort, @Nullable EntityGraph<T> graph);
}
