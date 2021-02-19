package net.digitallogic.ProjectManager.persistence.dto;

import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class UserDtoTest {

	@Test
	public void equalsAndHashEqualityTest() {
		UserDto user = UserFixtures.userDto();
		UserDto copy = UserDto.builder().id(user.getId()).build();

		assertThat(user).isEqualTo(copy);
		assertThat(user).hasSameHashCodeAs(copy);

		UserDto newUser = UserFixtures.userDto();
		assertThat(user).isNotEqualTo(newUser);
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		UserDto user = UserFixtures.userDto();
		UserDto copy = new UserDto(user);

		copy.setId(UUID.randomUUID());
		assertThat(copy).isNotEqualTo(user);
		assertThat(copy.hashCode()).isNotEqualTo(user.hashCode());
	}

	@Test
	public void copyConstructorTest() {
		UserDto user = UserFixtures.userDto();
		UserDto copy = new UserDto(user);

		assertThat(copy).isEqualToComparingFieldByField(user);
	}

	@Test
	public void mapEntityToDtoTest() {
		UserEntity entity = UserFixtures.userEntity();

		UserDto dto = new UserDto(entity);

		assertThat(dto).isEqualToComparingOnlyGivenFields(entity,
				"id", "email", "firstName", "lastName",
				"version", "createdDate", "lastModifiedDate");

		assertThat(dto.getRoles()).hasSameSizeAs(entity.getRoles());
		//assertThat(dto.getUserStatus()).hasSameSizeAs(entity.getUserStatus());
	}
}
