package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId_;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity_;
import net.digitallogic.ProjectManager.persistence.biTemporal.repository.BiTemporalRepository;
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
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoRepositoryBean
public class BiTemporalJpaRepository<T extends BiTemporalEntity<ID>, ID extends Serializable>
	extends SimpleJpaRepository<T, BiTemporalEntityId<ID>> implements BiTemporalRepository<T, ID> {

	private final String loadType = "javax.persistence.loadgraph";

	private final EntityManager entityManager;
	private final JpaEntityInformation<T, ?> entityInformation;

	public BiTemporalJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.entityManager = entityManager;
		this.entityInformation = entityInformation;
	}

	public BiTemporalJpaRepository(Class<T> domainClass, EntityManager entityManager) {
		this(JpaEntityInformationSupport
			.getEntityInformation(domainClass, entityManager), entityManager
		);
	}

	/* *** EntityGraphResolver Methods *** */
	public Optional<T> findById(ID id, @Nullable GraphResolver graph) {
		Assert.notNull(id, "The given id must not be null.");

		Class<T> clazz = getDomainClass();
		Map<String, Object> hints = new HashMap<>();
		hints.put(loadType, graph);

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

	// ** BiTemporal Methods ** //
	public Optional<T> findByEntityId(ID id, final LocalDateTime time) {
		return findOne(getById(id)
				.and(currentValidTimeSpec(time))
		);
	}

	public Optional<T> findByEntityId(ID id, final Clock clock) {
		return findByEntityId(id, LocalDateTime.now(clock));
	}

	public Iterable<T> findByEntityId(ID id, final LocalDateTime effectiveStart, final LocalDateTime effectiveStop) {
		return findByEntityId(id, effectiveStart, effectiveStop, Clock.systemUTC());
	}

	public Iterable<T> findByEntityId(ID id, final LocalDateTime effectiveStart, final LocalDateTime effectiveStop, Clock clock) {
		return findAll(
				getById(id)
						.and(validTimeSpec(effectiveStart, effectiveStop))
						.and(systemTimeSpec(LocalDateTime.now(clock)))
		);
	}

	public Iterable<T> getHistoryByEntityId(ID id, final LocalDateTime time) {
		return findAll(
				getById(id)
						.and(systemTimeSpec(time))
		);
	}

	public Iterable<T> getHistoryByEntityId(ID id, final Clock clock) {
		return getHistoryByEntityId(id, LocalDateTime.now(clock));
	}


	/* ** BiTemporal Specifications ** */
	public Specification<T> getById(final ID id) {
		return ((root, query, builder) ->
				builder.equal(
						root.get(BiTemporalEntity_.id)
								.get(BiTemporalEntityId_.id),
						id)
		);
	}

	@Nullable
	public Specification<T> currentValidTimeSpec(final LocalDateTime time) {
		return validTimeSpec(time)
				.and(systemTimeSpec(LocalDateTime.now(Clock.systemUTC())));
	}

	/* **  Get all valid entities with validStart and validStop  ** */
	public Specification<T> validTimeSpec(final LocalDateTime time) {
		return validTimeSpec(time, time);
	}

	public Specification<T> validTimeSpec(final LocalDateTime validStart,
	                                      final LocalDateTime validStop) {
		return ((root, query, builder) ->
				builder.and(
						builder.lessThanOrEqualTo(root.get(BiTemporalEntity_.id)
								.get(BiTemporalEntityId_.validStart), validStop),
						builder.greaterThan(root.get(BiTemporalEntity_.validStop), validStart)
				));
	}

	public Specification<T> systemTimeSpec(final LocalDateTime time) {
		return systemTimeSpec(time, time);
	}

	public Specification<T> systemTimeSpec(final LocalDateTime systemStart,
	                                       final LocalDateTime systemStop) {
		return ((root, query, builder) ->
				builder.and(
						builder.lessThanOrEqualTo(root.get(BiTemporalEntity_.id)
								.get(BiTemporalEntityId_.systemStart), systemStop),
						builder.greaterThan(root.get(BiTemporalEntity_.systemStop), systemStart)
				));
	}
}
