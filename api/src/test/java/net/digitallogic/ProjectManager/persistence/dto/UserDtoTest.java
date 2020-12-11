package net.digitallogic.ProjectManager.persistence.dto;

import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserDtoTest {

	@Test
	public void equalsAndHashTest() {
		UserDto user = UserFixtures.userDto();
		UserDto copy = UserDto.builder().id(user.getId()).build();

		assertThat(user).isEqualTo(copy);
		assertThat(user).hasSameHashCodeAs(copy);

		UserDto newUser = UserFixtures.userDto();
		assertThat(user).isNotEqualTo(newUser);
	}

	@Test
	public void entityToDtoTest() {
		UserEntity entity = UserFixtures.userEntity();

		UserDto dto = new UserDto(entity);

		assertThat(dto).isEqualToComparingOnlyGivenFields(entity,
				"id", "email", "firstName", "lastName",
				"version", "archived", "createdDate", "lastModifiedDate", "lastModifiedBy",
				"createdBy");
	}
}
