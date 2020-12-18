package net.digitallogic.ProjectManager.persistence.biTemporal.repository;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId_;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@NoRepositoryBean
public interface BiTemporalRepository<T extends BiTemporalEntity,
		ID extends Serializable> extends JpaSpecificationExecutor<T> {

	default Optional<T> getValidAtById(ID id, final LocalDateTime time) {
		return findOne(
				getById(id)
						.and(getValidAtTime(time))
		);
	}

	default Iterable<T> getHistoryById(ID id, final LocalDateTime time) {
		return findAll(
				getById(id)
						.and(getWithinSystemTime(time, time))
		);
	}

	default Specification<T> getById(final ID id) {
		return ((root, query, builder) ->
				builder.equal(root.get(BiTemporalEntity_.id).get(BiTemporalEntityId_.id),
						id));
	}

	default Specification<T> getValidAtTime(final LocalDateTime time) {
		return getValidAtTime(time, time);
	}

	default Specification<T> getValidAtTime(final LocalDateTime validTime,
	                                        final LocalDateTime systemTime) {
		return getWithinValidTime(validTime, validTime)
				.and(getWithinSystemTime(systemTime, systemTime));
	}

	default Specification<T> getWithinSystemTime(final LocalDateTime systemStart,
	                                             final LocalDateTime systemStop) {
		return ((root, query, builder) ->
				builder.and(
						builder.lessThanOrEqualTo(root.get(BiTemporalEntity_.id).get(BiTemporalEntityId_.systemStart), systemStart),
						builder.greaterThan(root.get(BiTemporalEntity_.systemStop), systemStart)
				));
	}

	default Specification<T> getWithinValidTime(final LocalDateTime validStart,
	                                            final LocalDateTime validStop) {
		return ((root, query, builder) ->
				builder.and(
						builder.lessThanOrEqualTo(root.get(BiTemporalEntity_.id).get(BiTemporalEntityId_.validStart), validStart),
						builder.greaterThan(root.get(BiTemporalEntity_.validStop), validStop)
				));
	}
}
