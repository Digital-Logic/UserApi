package net.digitallogic.ProjectManager.web.filter;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.config.RepositoryConfig;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.web.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;

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

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void firstNameEqualsFilterTest() {
		Stream.of("Sarah", "Howard", "Joe", "John")
				.forEach(name -> {
					List<UserEntity> result = userRepository.findAll(
							toSpecification("firstName==" + name)
					);
					assertThat(result).hasSize(1);
				});
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void firstNameLikeFilterTest() {
		Stream.of("Sara_", "Sar%", "%ward", "John")
				.forEach(name -> {
					List<UserEntity> results = userRepository.findAll(toSpecification("firstName=like=" + name));
					assertThat(results).hasSize(1);
				});
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void firstNameILikeFilterTest() {
		Stream.of("saRa_", "sar%", "%warD", "john")
				.forEach(name -> {
					List<UserEntity> results = userRepository.findAll(toSpecification("firstName=ilike=" + name));
					assertThat(results).hasSize(1);
				});
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void lastNameEqualsFilterTest() {
		Stream.of("Conner", "TheDuck", "Exotic", "Wick")
				.forEach(name -> {
					List<UserEntity> results = userRepository.findAll(
							toSpecification("lastName==" + name)
					);
					assertThat(results).hasSize(1);
				});
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void lastNameLikeFilterTest() {
		Stream.of("Conne_", "Con%", "%Duck", "Wick")
				.forEach(name -> {
					List<UserEntity> results = userRepository.findAll(
							toSpecification("lastName=like=" + name));

					assertThat(results).hasSize(1);
				});
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void lastNameILikeFilterTest() {
		Stream.of("coNne_", "con%", "%duck", "WICK")
				.forEach(name -> {
					List<UserEntity> results = userRepository.findAll(
							toSpecification("lastName=ilike=" + name));

					assertThat(results).hasSize(1);
				});
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateLessThanFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate<" +
						LocalDateTime.now(Clock.systemUTC())
								.plusMinutes(5)
				)
		);
		assertThat(results).hasSize(5);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateLessThanEqualToFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate<=" +
						LocalDateTime.now(Clock.systemUTC())
								.plusMinutes(5)
				)
		);
		assertThat(results).hasSize(5);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateGreaterThanFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate>" +
						LocalDateTime.now(Clock.systemUTC())
								.minusMinutes(5)
				)
		);
		log.error("TimeZone: " + TimeZone.getDefault());
		assertThat(results).hasSize(4);
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	public void createDateGreaterThanEqualToFilterTest() {
		List<UserEntity> results = userRepository.findAll(
				toSpecification("createdDate>=" +
						LocalDateTime.now(Clock.systemUTC())
								.minusMinutes(5)
				)
		);
		assertThat(results).hasSize(4);
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