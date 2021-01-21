package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoRepositoryBean
public class AdvancedJpaRepository<T, ID extends Serializable>
	extends SimpleJpaRepository<T, ID> implements EntityGraphRepository<T, ID> {

	private final String loadType = "javax.persistence.loadgraph";

	private final EntityManager entityManager;
	private final JpaEntityInformation<T, ?> entityInformation;

	public AdvancedJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.entityManager = entityManager;
		this.entityInformation = entityInformation;
	}

	public AdvancedJpaRepository(Class<T> domainClass, EntityManager entityManager) {
		this(JpaEntityInformationSupport
			.getEntityInformation(domainClass, entityManager), entityManager
		);
	}

	/* *** EntityGraphResolver Methods *** */
	public Optional<T> findById(ID id, @Nullable GraphResolver graph) {
		Assert.notNull(id, "The given id must not be null.");

		Class<T> clazz = getDomainClass();
		Map<String, Object> hints = new HashMap<>();

		if (graph != null)
			hints.put(loadType, graph.createGraph(entityManager));

		return Optional.ofNullable(entityManager.find(clazz, id, hints));
	}

	public Optional<T> findOne(@Nullable Specification<T> spec, @Nullable GraphResolver graphResolver) {
		try {
			TypedQuery<T> query = getQuery(spec, Sort.unsorted());

			if (graphResolver != null)
				query.setHint(loadType, graphResolver.createGraph(entityManager));

			return Optional.of(query.getSingleResult());
		} catch (NoResultException ex) {
			return Optional.empty();
		}
	}

	public List<T> findAll(@Nullable Specification<T> spec, @Nullable GraphResolver graphResolver) {
		TypedQuery<T> query = getQuery(spec, Sort.unsorted());

		if (graphResolver != null)
			query.setHint(loadType, graphResolver.createGraph(entityManager));

		return query.getResultList();
	}

	public List<T> findAll(@Nullable GraphResolver graphResolver) {
		TypedQuery<T> query = getQuery(null, Sort.unsorted());

		if (graphResolver != null)
			query.setHint(loadType, graphResolver.createGraph(entityManager));

		return query.getResultList();
	}

	public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable, @Nullable GraphResolver graphResolver) {
		TypedQuery<T> query = getQuery(spec, pageable);
		if (graphResolver != null)
			query.setHint(loadType, graphResolver.createGraph(entityManager));

		return pageable.isUnpaged() ? new PageImpl<>(query.getResultList())
				: readPage(query, getDomainClass(), pageable, spec);
	}

	public Page<T> findAll(Pageable pageable, @Nullable GraphResolver graphResolver) {
		if (pageable.isUnpaged()) {
			return new PageImpl<>(findAll(graphResolver));
		}

		return findAll(null, pageable, graphResolver);
	}

	public List<T> findAll(Sort sort, @Nullable GraphResolver graphResolver) {
		TypedQuery<T> query = getQuery(null, sort);

		if (graphResolver != null)
			query.setHint("javax.persistence.loadgraph", graphResolver.createGraph(entityManager));

		return query.getResultList();
	}
}
