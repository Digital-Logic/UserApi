package net.digitallogic.UserApi.persistence.entity.auth;

import net.digitallogic.UserApi.fixtures.RoleFixtures;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleEntityTest {

	@Test
	public void equalsAndHashEqualityTest() {
		RoleEntity entity = RoleFixtures.roleEntity();
		RoleEntity copy = RoleEntity.builder()
				.id(entity.getId())
				.build();

		assertThat(copy).isEqualTo(entity);
		assertThat(copy).hasSameHashCodeAs(entity);
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		RoleEntity entity = RoleFixtures.roleEntity();
		RoleEntity copy = new RoleEntity(entity);
		copy.setId(UUID.randomUUID());

		assertThat(copy).isNotEqualTo(entity);
		assertThat(copy.hashCode()).isNotEqualTo(entity.hashCode());
	}

	@Test
	public void copyConstructorTest() {
		RoleEntity entity = RoleFixtures.roleEntity();
		RoleEntity copy = new RoleEntity(entity);

		assertThat(copy).isEqualTo(entity);
		assertThat(copy).isEqualToComparingFieldByField(entity);
	}
}
