package net.digitallogic.UserApi.web.filter.operators;

import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class Like<T> extends Operator<T, String> {
	public Like() {
		super("=like=");
	}

	@Override
	public Predicate toPredicate(CriteriaBuilder builder, Root<T> root, @NonNull Path<String> path, List<String> args) {
		return builder.like(
				path,
				args.get(0)
					.replace("*", "%")
		);
	}
}
