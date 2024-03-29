package net.digitallogic.UserApi.web.filter.operators;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class Ilike<T> extends Operator<T, String> {
	public Ilike() {
		super("=ilike=");
	}

	@Override
	public Predicate toPredicate(CriteriaBuilder builder, Root<T> root, Path<String> path, List<String> args) {
		return builder.like(
				builder.upper(path),
				args.get(0).toUpperCase()
					.replace("*", "%")
		);
	}
}
