package net.digitallogic.ProjectManager.persistence.dto;

import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
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
}
