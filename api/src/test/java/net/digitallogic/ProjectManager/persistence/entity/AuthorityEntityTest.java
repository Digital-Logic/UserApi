package net.digitallogic.ProjectManager.persistence.entity;

import net.digitallogic.ProjectManager.fixtures.AuthorityFixtures;
import net.digitallogic.ProjectManager.persistence.entity.user.AuthorityEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorityEntityTest {

	@Test
	public void equalsAndHashEqualityTest() {
		AuthorityEntity entity = AuthorityFixtures.authorityEntity();
		AuthorityEntity copy = AuthorityEntity.builder()
				.id(entity.getId())
				.build();

		assertThat(copy).isEqualTo(entity);
		assertThat(copy).hasSameHashCodeAs(entity);
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		AuthorityEntity entity = AuthorityFixtures.authorityEntity();
		AuthorityEntity copy = new AuthorityEntity(entity);
		copy.setId(UUID.randomUUID());

		assertThat(copy).isNotEqualTo(entity);
		assertThat(copy.hashCode()).isNotEqualTo(entity.hashCode());
	}

	@Test
	public void copyConstructorTest() {
		AuthorityEntity entity = AuthorityFixtures.authorityEntity();
		AuthorityEntity copy = new AuthorityEntity(entity);

		assertThat(copy).isEqualTo(entity);
		assertThat(copy).isEqualToComparingFieldByField(entity);
	}
}
