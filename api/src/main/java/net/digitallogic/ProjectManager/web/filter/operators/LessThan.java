package net.digitallogic.ProjectManager.web.filter.operators;

import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class LessThan<T, P extends Comparable<? super P>> extends Operator<T, P> {

	public LessThan() {
		super("<", "=lt=");
	}

	@Override
	public Predicate toPredicate(CriteriaBuilder builder, Root<T> root, @NonNull Path<P> path, List<P> args) {
		return builder.lessThan(
				path,
				args.get(0)
		);
	}
}
