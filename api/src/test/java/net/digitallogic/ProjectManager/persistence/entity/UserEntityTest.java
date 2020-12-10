package net.digitallogic.ProjectManager.persistence.entity;

import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserEntityTest {

	@Test
	public void equalsAndHashTest() {
		UserEntity user = UserFixtures.userEntity();
		UserEntity copy = UserEntity.builder().id(user.getId()).build();

		assertThat(user).isEqualTo(copy);
		assertThat(user).hasSameHashCodeAs(copy);

		assertThat(user).isNotEqualTo(UserFixtures.userEntity());
	}
}
