package net.digitallogic.ProjectManager.web.filter.operators;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;


@Slf4j
@EqualsAndHashCode(of = "operator")
public abstract class Operator<T,P> {

	@Getter
	private final ComparisonOperator operator;

	public Operator(String... symbols) {
		this.operator = new ComparisonOperator(symbols);
	}

	public abstract Predicate toPredicate(CriteriaBuilder builder, Root<T> root, Path<P> path, List<P> args);
}
