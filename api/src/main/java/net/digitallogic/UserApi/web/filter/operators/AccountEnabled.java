package net.digitallogic.UserApi.web.filter.operators;

import net.digitallogic.UserApi.persistence.biTemporal.entity.BiTemporalEntityId_;
import net.digitallogic.UserApi.persistence.biTemporal.entity.BiTemporalEntity_;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity_;
import net.digitallogic.UserApi.persistence.entity.user.UserStatusEntity;
import net.digitallogic.UserApi.persistence.entity.user.UserStatusEntity_;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class AccountEnabled extends Operator<UserEntity, LocalDateTime> {
	public AccountEnabled() {
		super("==");
	}

	@Override
	public Predicate toPredicate(CriteriaBuilder builder, Root<UserEntity> root, @Nullable Path<LocalDateTime> path, List<LocalDateTime> args) {

		Join<UserEntity, UserStatusEntity> join = root.join(UserEntity_.userStatus);

		return builder.and(
			builder.equal(join.get(UserStatusEntity_.accountEnabled), true),
				builder.lessThanOrEqualTo(join.get(BiTemporalEntity_.id).get(BiTemporalEntityId_.validStart), args.get(0)),
				builder.greaterThan(join.get(BiTemporalEntity_.validStop), LocalDateTime.now(Clock.systemUTC())),
				builder.lessThanOrEqualTo(join.get(BiTemporalEntity_.id).get(BiTemporalEntityId_.systemStart), args.get(0)),
				builder.greaterThan(join.get(BiTemporalEntity_.systemStop), LocalDateTime.now(Clock.systemUTC()))
		);
	}
}
