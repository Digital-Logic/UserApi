package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId_;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity_;
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

import javax.persistence.EntityGraph;
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
	extends SimpleJpaRepository<T, BiTemporalEntityId<ID>> implements EntityGraphRepository<T, ID> {

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

	/* *** *** */

//	@Transactional
//	public <S extends T> S save(S entity) {
//		System.out.println("Saving Entity!");
//
//		BiTemporalEntityId<ID> id = entity.getId();
//		if (id.getId()== null) {
//			System.out.println("ID valid is null!");
//		}
//		Class<T> clazz = entityInformation.getJavaType();

//		Field mapsIdField = findIdField(clazz)
//				.orElseThrow();
//
//		try {
//			BeanInfo info = Introspector.getBeanInfo(clazz, Object.class);
//			for (PropertyDescriptor pd:info.getPropertyDescriptors()) {
//				if (pd.getName().equals(mapsIdField.getName())) {
//					System.out.println("Getter: " + pd.getReadMethod().getName());
//					Method getUser = pd.getReadMethod();
//					getUser.invoke(entity);
//				}
//			}
//
//		} catch (IntrospectionException |
//				IllegalAccessException |
//				InvocationTargetException ex) {
//			ex.printStackTrace();
//		}

//		List<T> curEntities = findAll(
//			getById(entity.getEmbeddedId())
//				.and(fetchByValidTime(entity.getValidStart(), entity.getValidStop()))
//				.and(fetchBySystemTime(LocalDateTime.now(Clock.systemUTC())))
//		);
//
//		System.out.println("List size: " + curEntities.size());
//		curEntities.forEach(e -> {
//			System.out.println("Entity: " + e.getId() + ", validStart: " + e.getValidStart());
//		});
//
//		entityManager.persist(entity);
//		return entity;
//	}

//	protected Optional<Field> findIdField(Class clazz) {
//		Field[] fields = clazz.getDeclaredFields();
//		for (Field field:fields) {
//			if (field.getAnnotation(MapsId.class) != null)
//				return Optional.of(field);
//		}
//
//		if (clazz.getSuperclass() != Object.class) {
//			return findIdField(clazz.getSuperclass());
//		}
//		return Optional.empty();
//	}


	/* *** EntityGraph Methods *** */
	public Optional<T> findById(ID id, @Nullable EntityGraph<T> graph) {
		Assert.notNull(id, "The given id must not be null.");

		Class<T> clazz = getDomainClass();
		Map<String, Object> hints = new HashMap<>();
		hints.put(loadType, graph);

		return Optional.ofNullable(entityManager.find(clazz, id, hints));
	}

	public Optional<T> findOne(@Nullable Specification<T> spec, @Nullable EntityGraph<T> graph) {
		try {
			TypedQuery<T> query = getQuery(spec, Sort.unsorted());
			query.setHint(loadType, graph);

			return Optional.of(query.getSingleResult());
		} catch (NoResultException ex) {

			return Optional.empty();
		}
	}

	public List<T> findAll(@Nullable Specification<T> spec, @Nullable EntityGraph<T> graph) {
		TypedQuery<T> query = getQuery(spec, Sort.unsorted());
		query.setHint(loadType, graph);

		return query.getResultList();
	}

	public List<T> findAll(@Nullable EntityGraph<T> graph) {
		TypedQuery<T> query = getQuery(null, Sort.unsorted());
		query.setHint(loadType, graph);

		return query.getResultList();
	}

	public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable, @Nullable EntityGraph<T> graph) {
		TypedQuery<T> query = getQuery(spec, pageable);
		query.setHint(loadType, graph);

		return pageable.isUnpaged() ? new PageImpl<>(query.getResultList())
				: readPage(query, getDomainClass(), pageable, spec);
	}

	public Page<T> findAll(Pageable pageable, @Nullable EntityGraph<T> graph) {
		if (pageable.isUnpaged()) {
			return new PageImpl<>(findAll(graph));
		}

		return findAll(null, pageable, graph);
	}

	public List<T> findAll(Sort sort, @Nullable EntityGraph<T> graph) {
		TypedQuery<T> query = getQuery(null, sort);
		query.setHint("javax.persistence.loadgraph", graph);

		return query.getResultList();
	}

	public Optional<T> findById(ID id, final LocalDateTime time) {
		return findOne(getById(id)
				.and(fetchCurrentByValidTime(time))
		);
	}

	public Iterable<T> getHistoryById(ID id, final LocalDateTime time) {
		return findAll(
				getById(id)
						.and(fetchBySystemTime(time))
		);
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

	public Specification<T> fetchCurrentByValidTime(final LocalDateTime time) {
		return fetchByValidTime(time)
				.and(fetchBySystemTime(LocalDateTime.now(Clock.systemUTC())));
	}

	/* **  Get all valid entities with validStart and validStop  ** */
	public Specification<T> fetchByValidTime(final LocalDateTime time) {
		return fetchByValidTime(time, time);
	}

	public Specification<T> fetchByValidTime(final LocalDateTime validStart,
	                                          final LocalDateTime validStop) {
		return ((root, query, builder) ->
				builder.and(
						builder.lessThanOrEqualTo(root.get(BiTemporalEntity_.id).get(BiTemporalEntityId_.validStart), validStop),
						builder.greaterThan(root.get(BiTemporalEntity_.validStop), validStart)
				));
	}

	public Specification<T> fetchBySystemTime(final LocalDateTime time) {
		return fetchBySystemTime(time, time);
	}

	public Specification<T> fetchBySystemTime(final LocalDateTime systemStart,
	                                           final LocalDateTime systemStop) {
		return ((root, query, builder) ->
				builder.and(
						builder.lessThanOrEqualTo(root.get(BiTemporalEntity_.id).get(BiTemporalEntityId_.systemStart), systemStop),
						builder.greaterThan(root.get(BiTemporalEntity_.systemStop), systemStart)
				));
	}
}
