package net.digitallogic.ProjectManager.web.exceptions;

public class InvalidComparisonOperator extends BadRequestException {
	public InvalidComparisonOperator(String property) {
		super(
				new StringBuilder()
						.append("Invalid filter operation on property ")
						.append(property)
						.append(".")
						.toString()
		);
	}
}
