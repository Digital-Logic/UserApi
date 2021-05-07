package net.digitallogic.UserApi.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Stream;

import static net.digitallogic.UserApi.services.Utils.processSortBy;
import static net.digitallogic.UserApi.services.Utils.toCamel;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

	@ParameterizedTest(name ="processSortBy {0}")
	@MethodSource
	void processSortByTest(String sortString, List<String> actual) {
		List<Sort.Order> result = processSortBy(sortString);
		assertThat(result).extracting("property")
				.containsSequence(actual);
	}

	private static Stream<Arguments> processSortByTest() {
		return Stream.of(
				Arguments.of("first_name", List.of("firstName")),
				Arguments.of(" first_name , last_name ", List.of("firstName", "lastName")),
				Arguments.of("  firstName, lastName ", List.of("firstName", "lastName")),
				Arguments.of("  first_name , lastName", List.of("firstName", "lastName"))
		);
	}


	@ParameterizedTest(name = "toCamelCase {0}")
	@MethodSource
	void toCamelTest(String strToConvert, String actual) {
		assertThat(toCamel(strToConvert)).isEqualTo(actual);
	}

	private static Stream<Arguments> toCamelTest() {
		return Stream.of(
				Arguments.of("firstName", "firstName"),
				Arguments.of(" first_name ", "firstName"),
				Arguments.of("first_name ", "firstName"),
				Arguments.of("first_name ", "firstName"),
				Arguments.of("firstName  ", "firstName"),
				Arguments.of("  firstName", "firstName")
		);
	}
}
