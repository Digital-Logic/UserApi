package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.services.Utils;
import net.digitallogic.ProjectManager.web.error.ErrorMessage;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import net.digitallogic.ProjectManager.web.error.exceptions.Error;
import org.springframework.lang.Nullable;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class GraphBuilder<T> {

	private final Class<T> entityType;
	private final Map<String, Consumer<EntityGraph<T>>> graphMapper;

	private GraphBuilder(Class<T> entityType,
	                     Map<String, Consumer<EntityGraph<T>>> graphMapper) {
		this.entityType = entityType;
		this.graphMapper = graphMapper;
	}

	@Nullable
	public GraphResolver createResolver(@Nullable String expandAttributes) {
		if (expandAttributes == null || expandAttributes.isBlank())
			return null;

		return new GraphResolver(expandAttributes);
	}


	public static <E>EntityGraphBuilder<E> builder(Class<E> type) {
		return new EntityGraphBuilder<>(type);
	}

	public class GraphResolver {
		private final List<String> properties;

		public GraphResolver(String expandAttributes) {
			// Split attributes list and transform to camelCase if needed.
			properties = Utils.toCamel(
					expandAttributes.split("\\s*,\\s*"));

			List<Error> errors = properties.stream()
					.filter(Predicate.not(graphMapper::containsKey))
					.map(property ->
						new Error(property,
								ErrorMessage.InvalidExpansionProperty())
					)
					.collect(Collectors.toList());

			if (!errors.isEmpty()) {
				throw new BadRequestException(
						ErrorMessage.InvalidExpansionProperty(),
						errors
				);
			}
		}

		public EntityGraph<T> createGraph(EntityManager entityManager) {

			final EntityGraph<T> graph = entityManager.createEntityGraph(entityType);

			properties.stream()
					.map(graphMapper::get)
					.forEach(g -> g.accept(graph));

			return graph;
		}
	}

	public static class EntityGraphBuilder<T> {

		private final Class<T> entityType;
		private final Map<String, Consumer<EntityGraph<T>>> graphMapper = new HashMap<>();

		private EntityGraphBuilder(Class<T> entityType) {
			this.entityType = entityType;
		}

		public EntityGraphBuilder<T> addProperty(String property) {
			return addProperty(property, graph -> graph.addSubgraph(property));
		}

		public EntityGraphBuilder<T> addProperty(String property, Consumer<EntityGraph<T>> mapper) {
			graphMapper.put(property, mapper);
			return this;
		}

		public GraphBuilder<T> build() {
			return new GraphBuilder<>(entityType, graphMapper);
		}
	}
}
