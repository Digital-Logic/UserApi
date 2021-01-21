package net.digitallogic.ProjectManager.web.filter;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import net.digitallogic.ProjectManager.converters.LocalDateTimeConverter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpecSupport {

	private static ConversionService converter;

	public static ConversionService getConverter() {
		if (converter == null) {
			synchronized (SpecSupport.class) {
				if (converter == null) {
					DefaultConversionService cs = new DefaultConversionService();
					cs.addConverter(new LocalDateTimeConverter());

					converter = cs;
				}
			}
		}
		return converter;
	}

	public static void setConverter(ConversionService converter) {
		synchronized (SpecSupport.class) {
			SpecSupport.converter = converter;
		}
	}

	@Nullable
	public static <T> Specification<T> toSpecification(@Nullable String queryStr) {
		if (queryStr == null)
			return null;

		return ((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {

			// get all available operators for this class
			Set<ComparisonOperator> operators = Visitor.getOperators(root.getJavaType());

			Node rootNode = new RSQLParser(operators).parse(queryStr);

			return rootNode.accept(new Visitor<>(builder), root);
		});
	}


	// **** ** Filter builders ** **** //
	public static <T>FilterBuilder<T> addFilter(Class<T> entityType) {
		return new FilterBuilder<>(entityType);
	}

	public static class FilterBuilder<T> {
		private final Class<T> entityType;
		public final Map<String, PropConverter<?,?>> properties = new HashMap<>();

		private FilterBuilder(Class<T> entityType) {
			this.entityType = entityType;
		}

		public <P>PropConverter.Builder<T,P> addProperty(String property, Class<P> type) {
			return PropConverter.builder(type, property, this);
		}

		public FilterBuilder<T> addProperty(PropConverter<?,?> converter) {
			properties.put(converter.getProperty(), converter);
			return this;
		}

		public void build() {
			Visitor.setConverter(entityType, properties);
		}
	}
}
