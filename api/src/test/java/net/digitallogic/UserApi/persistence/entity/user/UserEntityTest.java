package net.digitallogic.UserApi.persistence.entity.user;

import net.digitallogic.UserApi.fixtures.UserFixtures;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class UserEntityTest {

	@Test
	public void equalsAndHashEqualityTest() {
		UserEntity user = UserFixtures.userEntity();
		UserEntity copy = UserEntity.builder().id(user.getId()).build();

		assertThat(user).isEqualTo(copy);
		assertThat(user).hasSameHashCodeAs(copy);

		assertThat(user).isNotEqualTo(UserFixtures.userEntity());
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		UserEntity user = UserFixtures.userEntity();
		UserEntity copy = new UserEntity(user);
		copy.setId(UUID.randomUUID());

		assertThat(copy).isNotEqualTo(user);
		assertThat(copy.hashCode()).isNotEqualTo(user.hashCode());
	}

	@Test
	public void copyConstructorTest() {
		UserEntity user = UserFixtures.userEntity();
		UserEntity copy = new UserEntity(user);

		assertThat(copy).isEqualTo(user);
		assertThat(copy).hasSameHashCodeAs(user);
		assertThat(copy).isEqualToComparingFieldByField(user);
	}
}
