package net.digitallogic.UserApi.fixtures;

import com.github.javafaker.Faker;
import net.digitallogic.UserApi.persistence.dto.user.CreateUserRequest;
import net.digitallogic.UserApi.persistence.dto.user.UserDto;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;

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
				.createdDate(LocalDateTime.now(clock))
				.lastModifiedDate(LocalDateTime.now(clock))
				.build();
	}

	public static List<UserEntity> userEntity(int size) {
		return Stream.generate(UserFixtures::userEntity)
				.limit(size)
				.collect(Collectors.toList());
	}

	public static CreateUserRequest createUser() { return createUser(clock); }
	public static CreateUserRequest createUser(Clock clock) {
		return CreateUserRequest.builder()
				.email(faker.internet().emailAddress())
				.firstName(faker.name().firstName())
				.lastName(faker.name().lastName())
				.password(faker.internet().password())
				.build();
	}
}
