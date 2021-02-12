package net.digitallogic.ProjectManager.web.filter;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.config.RepositoryConfig;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import static net.digitallogic.ProjectManager.web.filter.SpecSupport.toSpecification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RepositoryTest
@Slf4j
public class UserFilterTest {

	@Autowired
	UserRepository userRepository;

	@BeforeAll
	public static void beforeAll() {
		RepositoryConfig.configUserEntityFilters();
		TimeZone timeZone = TimeZone.getDefault();
		System.out.println("Current timezone: " + timeZone);
	}

	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"Sarah", "Howard", "Joe", "John"})
	public void firstNameEqualsFilterTest(String queryName) {

		List<UserEntity> result = userRepository.findAll(
				toSpecification("firstName==" + queryName)
		);
		assertThat(result).hasSize(1);
	}

	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"Sara_", "Sar%", "%ward", "John"})
	public void firstNameLikeFilterTest(String queryName) {
		List<UserEntity> results = userRepository.findAll(toSpecification("firstName=like=" + queryName));
		assertThat(results).hasSize(1);
	}

	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"saRa_", "sar%", "%warD", "john"})
	public void firstNameILikeFilterTest(String queryName) {

		List<UserEntity> results = userRepository.findAll(toSpecification("firstName=ilike=" + queryName));
		assertThat(results).hasSize(1);
	}

	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"Conner", "TheDuck", "Exotic", "Wick"})
	public void lastNameEqualsFilterTest(String queryName) {

		List<UserEntity> results = userRepository.findAll(
				toSpecification("lastName==" + queryName)
		);
		assertThat(results).hasSize(1);

	}

	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"Conne_", "Con%", "%Duck", "Wick"})
	public void lastNameLikeFilterTest(String queryName) {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("lastName=like=" + queryName));

		assertThat(results).hasSize(1);
	}

	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"coNne_", "con%", "%duck", "WICK"})
	public void lastNameILikeFilterTest(String queryName) {

		List<UserEntity> results = userRepository.findAll(
				toSpecification("lastName=ilike=" + queryName));

		assertThat(results).hasSize(1);
	}

	@ParameterizedTest
	@ValueSource(strings = {"lastName<=joe", "createdDate==today", "accountEnabled==true", "lastName=ilike="})
	public void invalidFilterTest(String filter){
		assertThatThrownBy(() ->
					userRepository.findAll(toSpecification(filter))
				).isInstanceOf(BadRequestException.class);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateLessThanFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate<" +
						LocalDateTime.of(2020, 1, 1, 1, 0))
		);
		assertThat(results).hasSize(1);
	}


	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateLessThanEqualToFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate<=" +
						LocalDateTime.of(2020, 1, 1, 0, 0))
		);
		assertThat(results).hasSize(1);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateGreaterThanFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate>" +
						LocalDateTime.of(2020, 3, 1, 1, 0))
		);
		log.info("TimeZone: " + TimeZone.getDefault());
		assertThat(results).hasSize(2);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateGreaterThanEqualToFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate>=" +
						LocalDateTime.of(2020, 3, 1, 0, 0))
		);
		assertThat(results).hasSize(3);
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	public void accountEnabledTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("accountEnabled==" + LocalDateTime.now()
						.toString())
		);
		assertThat(results).hasSize(1);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void invalidFilterPropertyTest() {
		assertThatThrownBy(() -> userRepository.findAll(
				toSpecification("password==password")
		)).isInstanceOf(BadRequestException.class);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void invalidComparisonOperatorTest() {
		assertThatThrownBy(() -> userRepository.findAll(
				toSpecification("firstName<=Joe")
		)).isInstanceOf(BadRequestException.class);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void invalidArgumentConversionTest() {
		assertThatThrownBy(() -> userRepository.findAll(
				toSpecification("accountEnabled==true")
		)).isInstanceOf(BadRequestException.class);
	}
}