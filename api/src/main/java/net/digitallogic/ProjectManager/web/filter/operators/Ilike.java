package net.digitallogic.ProjectManager.web.filter.operators;

import org.springframework.lang.NonNull;

import javax.persistence.criteria.*;
import java.util.List;

public class Ilike<T> extends Operator<T, String> {
	public Ilike() {
		super("=ilike=");
	}

	@Override
	public Predicate toPredicate(CriteriaBuilder builder, Root<T> root, @NonNull Path<String> path, List<String> args) {
		return builder.like(
				builder.upper(path),
				args.get(0).toUpperCase()
		);
	}
}
