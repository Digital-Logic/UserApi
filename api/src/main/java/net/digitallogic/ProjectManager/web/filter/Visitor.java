package net.digitallogic.ProjectManager.web.filter;

import cz.jirutka.rsql.parser.ast.*;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static net.digitallogic.ProjectManager.services.Utils.toCamel;

@Slf4j
public class Visitor<T> implements RSQLVisitor<Predicate, Root<T>> {

	private static final Map<Class<?>, Map<String, PropConverter<?,?>>> converters = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Set<ComparisonOperator>> operators = new ConcurrentHashMap<>();

	public static void setConverter(@NonNull Class<?> clazz, @NonNull Map<String, PropConverter<?,?>> properties) {
		requireNonNull(clazz);
		requireNonNull(properties);

		if (converters.putIfAbsent(clazz, properties) == null) {
			// update operators Map
			operators.putIfAbsent(clazz,
					converters.get(clazz).values().stream()
							.map(PropConverter::getOperators)
							.flatMap(Collection::stream)
							.collect(Collectors.toSet())
			);
		}
	}

	public static Set<ComparisonOperator> getOperators(Class<?> clazz) {
		return operators.get(clazz);
	}

	private final CriteriaBuilder builder;

	public Visitor(CriteriaBuilder builder) {
		this.builder = builder;
	}

	@Override
	public Predicate visit(AndNode node, Root<T> root) {
		log.debug("Visit(node: {}, root: {})", node, root);
		return node.getChildren()
				.stream()
				.map(n -> n.accept(this, root))
				.reduce(builder::and)
				.get(); // TODO fix isPresent check
	}

	@Override
	public Predicate visit(OrNode node, Root<T> root) {
		log.debug("Visit(node: {}, root: {})", node, root);
		return node.getChildren()
				.stream()
				.map(n -> n.accept(this, root))
				.reduce(builder::or)
				.get(); // TODO fix isPresent check
	}

	@Override
	@SuppressWarnings("unchecked")
	public Predicate visit(ComparisonNode node, Root<T> root) {
		log.debug("Visit(node: {}, root: {})", node, root);

		Class<? extends T> clazz = root.getJavaType();

		try {
			PropConverter<T, ?> converter = (PropConverter<T, ?>) converters.get(clazz)
					.get(toCamel(node.getSelector()));

			return converter
					.toPredicate(builder, root, node.getOperator(), node.getArguments());

		} catch (NullPointerException ex) {
			throw new BadRequestException(
					ErrorCode.INVALID_PROPERTY,
					MessageTranslator.InvalidFilterProperty(node.getSelector())
			);
		}
	}
}
