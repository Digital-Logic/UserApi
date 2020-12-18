package net.digitallogic.ProjectManager.fixtures;

import com.github.javafaker.Faker;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserFixtures {

	private static final Faker faker = new Faker();
	private static final Clock clock = Clock.systemUTC();
	private static final Random random = new Random();

	public static UserDto userDto() { return userDto(clock); }
	public static UserDto userDto(Clock clock) {
		return UserDto.builder()
				.id(UUID.randomUUID())
				.firstName(faker.name().firstName())
				.lastName(faker.name().lastName())
				.email(faker.internet().emailAddress())
				.roles(RoleFixtures.roleDto(random.nextInt(2)))
				.userStatus(List.of(

				))
				.createdBy(UUID.randomUUID())
				.lastModifiedBy(UUID.randomUUID())
				.createdDate(LocalDateTime.now(clock))
				.lastModifiedDate(LocalDateTime.now(clock))
				.build();
	}

	public static List<UserDto> userDto(int size) {
		return Stream.generate(UserFixtures::userDto)
				.limit(size)
				.collect(Collectors.toList());
	}

	public static UserEntity userEntity() { return userEntity(clock); }
	public static UserEntity userEntity(Clock clock) {
		return UserEntity.builder()
				.id(UUID.randomUUID())
				.firstName(faker.name().firstName())
				.lastName(faker.name().lastName())
				.email(faker.internet().emailAddress())
				.password(faker.internet().password())
				.roles(new HashSet<>(RoleFixtures.roleEntity(random.nextInt(2))))
				.createdBy(UUID.randomUUID())
				.lastModifiedBy(UUID.randomUUID())
				.createdDate(LocalDateTime.now(clock))
				.lastModifiedDate(LocalDateTime.now(clock))
				.build();
	}

	public static List<UserEntity> userEntity(int size) {
		return Stream.generate(UserFixtures::userEntity)
				.limit(size)
				.collect(Collectors.toList());
	}
}
