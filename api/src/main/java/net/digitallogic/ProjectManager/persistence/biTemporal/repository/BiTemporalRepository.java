package net.digitallogic.ProjectManager.persistence.biTemporal.repository;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@NoRepositoryBean
public interface BiTemporalRepository<T extends BiTemporalEntity<ID>, ID extends Serializable>
		extends Repository<T, BiTemporalEntityId<ID>>, JpaSpecificationExecutor<T> {

	<S extends T> S save(S entity);
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);
	Iterable<T> findAll();
	long count();

	/* ** BiTemporal Specifications ** */
	Optional<T> findById(ID id, final LocalDateTime time);
	Iterable<T> getHistoryById(ID id, final LocalDateTime time);

	Specification<T> getById(final ID id);
	Specification<T> currentValidTimeSpec(final LocalDateTime time);

	Specification<T> validTimeSpec(final LocalDateTime time);
	Specification<T> validTimeSpec(final LocalDateTime validStart,
	                                  final LocalDateTime validStop);

	Specification<T> systemTimeSpec(final LocalDateTime time);
	Specification<T> systemTimeSpec(final LocalDateTime systemStart, final LocalDateTime systemStop);

}
