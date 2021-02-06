package net.digitallogic.ProjectManager.web.filter;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.web.exceptions.BadRequestException;
import net.digitallogic.ProjectManager.web.exceptions.MessageCode;
import net.digitallogic.ProjectManager.web.filter.SpecSupport.FilterBuilder;
import net.digitallogic.ProjectManager.web.filter.operators.Operator;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.digitallogic.ProjectManager.web.filter.SpecSupport.getConverter;

@Slf4j
@EqualsAndHashCode(of = {"property", "targetType"})
public class PropConverter<T, P> {

	private final Map<ComparisonOperator, Operator<T,P>> operators;

	@Getter
	private final String property;
	private final Class<P> targetType; // property type - used for conversion
	private final Function<Root<T>, Path<P>> pathMapper;
	private final boolean resolvePath;

	private PropConverter(String property, Function<Root<T>, Path<P>> pathMapper, Class<P> targetType, Set<Operator<T,P>> operators, boolean resolvePath) {
		this.property = property;
		this.pathMapper = pathMapper;
		this.targetType = targetType;
		this.operators = operators.stream()
				.collect(Collectors.toUnmodifiableMap(Operator::getOperator, Function.identity()));
		this.resolvePath = resolvePath;
	}

	public Set<ComparisonOperator> getOperators() {
		return operators.keySet();
	}


	public Predicate toPredicate(CriteriaBuilder builder, Root<T> root, ComparisonOperator operator, List<String> args) {
		ConversionService converter = getConverter();

		// Convert Args to target type
		List<P> objArgs;
		try {
			// Catch conversion exceptions
			objArgs = args.stream()
					.map(arg -> converter.convert(arg, targetType))
					.collect(Collectors.toList());
		} catch (ConversionFailedException ex) {
			throw new BadRequestException(MessageCode.FILTER_ARG_CONVERSION,
					List.of(
							ex.getValue() != null ? ex.getValue().toString() : "null",
							ex.getTargetType().getType().getSimpleName()),
					ex
			);
		} catch (ConversionException ex) {
			log.error("Conversion service throw ConversionException error: " + ex.getMessage());
			throw new BadRequestException(MessageCode.TYPE_CONVERSION_ERROR);
		}

		// TODO fix nullable path
		Path<P> path = null;

		if (resolvePath) {
			path = pathMapper.apply(root);
		}
		try {
			return operators.get(operator)
					.toPredicate(builder, root, path, objArgs);
		} catch (NullPointerException ex) {
			throw new BadRequestException(MessageCode.FILTER_INVALID_COMPARISON_OPERATOR);
		}
	}

	public static <T,P>Builder<T,P> builder(Class<P> type, String property, FilterBuilder<T> filterBuilder) {
		return new Builder<>(type, property, filterBuilder);
	}

	public static class Builder<T, P> {
		private final FilterBuilder<T> filterBuilder;
		private final Class<P> type;
		@Getter
		private final String property;
		private final Set<Operator<T,P>> operators = new HashSet<>();
		private Function<Root<T>, Path<P>> pathMapper;
		private boolean resolvePath;

		private Builder(Class<P> type, String property, FilterBuilder<T> filterBuilder) {
			this.type = type;
			this.property = property;
			this.filterBuilder = filterBuilder;
			this.resolvePath = true;
			this.pathMapper = root -> root.get(property);
		}

		public Builder<T,P> setPath(Function<Root<T>, Path<P>> pathMapper) {
			this.pathMapper = pathMapper;
			resolvePath = true;
			return this;
		}

		public Builder<T,P> resolvePath(boolean resolve) {
			this.resolvePath = resolve;
			return this;
		}

		public Builder<T,P> addComparator(Operator<T,P> operator) {
			operators.add(operator);
			return this;
		}

		public FilterBuilder<T> build() {
			return filterBuilder.addProperty(
					new PropConverter<>(property, pathMapper,
							type, operators, resolvePath)
			);
		}
	}
}
