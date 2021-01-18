package net.digitallogic.ProjectManager.web.exceptions;

import cz.jirutka.rsql.parser.ast.ComparisonNode;

public class InvalidFilterProperty extends BadRequestException {
	private ComparisonNode node;

	public InvalidFilterProperty(ComparisonNode node) {
		super(new StringBuilder()
				.append("Cannot filter on an invalid property ")
				.append(node.getSelector())
				.append(".")
				.toString());
		this.node = node;
	}
}
