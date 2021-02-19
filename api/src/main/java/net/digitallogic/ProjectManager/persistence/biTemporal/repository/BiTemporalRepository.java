package net.digitallogic.ProjectManager.persistence.biTemporal.repository;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@NoRepositoryBean
public interface BiTemporalRepository<T extends BiTemporalEntity<ID>, ID extends Serializable>
		extends Repository<T, BiTemporalEntityId<ID>>, JpaSpecificationExecutor<T> {

	<S extends T> S save(S entity); // Uses Clock.systemUTC to set system time information.
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);
	Iterable<T> findAll();
	long count();

	/* ** BiTemporal Specifications ** */
	Optional<T> findByEntityId(ID id, final Clock clock);
	Optional<T> findByEntityId(ID id, final LocalDateTime time);


	// Get all entities by entity id, with and an valid time between effetiveStart and effectiveStop
	Iterable<T> findByEntityId(ID id, final LocalDateTime effectiveStart, final LocalDateTime effectiveStop);

	Iterable<T> getHistoryByEntityId(ID id, final Clock clock);
	Iterable<T> getHistoryByEntityId(ID id, final LocalDateTime time);

	Specification<T> getById(final ID id);
	Specification<T> currentValidTimeSpec(final LocalDateTime time);

	Specification<T> validTimeSpec(final LocalDateTime time);
	Specification<T> validTimeSpec(final LocalDateTime validStart,
	                                  final LocalDateTime validStop);

	Specification<T> systemTimeSpec(final LocalDateTime time);
	Specification<T> systemTimeSpec(final LocalDateTime systemStart, final LocalDateTime systemStop);

}
