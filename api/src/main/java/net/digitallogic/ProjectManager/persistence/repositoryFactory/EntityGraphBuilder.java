package net.digitallogic.ProjectManager.persistence.repositoryFactory;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

public class EntityGraphBuilder<T> {

	private final EntityManager entityManager;
	private final Class<T> clazz;
	private final Map<String, Consumer<EntityGraph<T>>> mapper;

	public EntityGraphBuilder(EntityManager entityManager,
	                          Class<T> clazz,
	                          Map<String, Consumer<EntityGraph<T>>> mapper) {

		this.entityManager = entityManager;
		this.clazz = clazz;
		this.mapper = mapper;
	}

	public EntityGraph<T> createEntityGraph(String expandAttributes) {
		EntityGraph<T> graph = entityManager.createEntityGraph(clazz);

		Arrays.stream(expandAttributes.split(","))
				.map(String::toLowerCase)
				.map(String::trim)
				.forEach(att -> {
					if (mapper.containsKey(att)) {
						mapper.get(att).accept(graph);
					}
				});

		return graph;
	}
}
