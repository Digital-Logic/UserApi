package net.digitallogic.ProjectManager.services;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;

import static net.digitallogic.ProjectManager.services.Utils.processSortBy;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

	@Test
	void processSortByTest() {
		List<Sort.Order> result = processSortBy("last_name, first_name");
		assertThat(result).extracting("property")
				.containsExactly("lastName", "firstName");
	}
}
