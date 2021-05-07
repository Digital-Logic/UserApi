package net.digitallogic.UserApi.web.filter.operators;

import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class Equals<T, P> extends Operator<T, P> {
	public Equals() {
		super("==","=eq=");
	}

	@Override
	public Predicate toPredicate(CriteriaBuilder builder, Root<T> root, @NonNull Path<P> path, List<P> args) {
		return builder.equal(
			path,
			args.get(0)
		);
	}
}
