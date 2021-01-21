package net.digitallogic.ProjectManager.persistence.entity;

import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatusEntityTest {

	@Test
	public void equalsAndHashEqualityTest() {
		UserEntity user = UserFixtures.userEntity();
		UserStatusEntity status = UserStatusEntity.builder()
				.id(new BiTemporalEntityId<>(user.getId()))
				.user(user)
				.accountEnabled(true)
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.createdBy(UUID.randomUUID())
				.build();

		UserStatusEntity copy = UserStatusEntity.builder()
				.id(new BiTemporalEntityId<>(user.getId(), status.getValidStart()))
				.build();

		assertThat(copy).isEqualTo(status);
		assertThat(copy).hasSameHashCodeAs(status);
	}

	@Test
	public void copyConstructor() {
		UserEntity user = UserFixtures.userEntity();
		UserStatusEntity status = UserStatusEntity.builder()
				.accountEnabled(true)
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.createdBy(UUID.randomUUID())
				.build();
		//user.addUserStatus(status);

		UserStatusEntity copy = new UserStatusEntity(status);

		assertThat(copy).isEqualTo(status);
		assertThat(copy).hasSameHashCodeAs(status);
		assertThat(copy).isEqualToComparingFieldByField(status);
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		UserEntity user = UserFixtures.userEntity();
		UserStatusEntity status = UserStatusEntity.builder()
				.accountEnabled(true)
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.createdBy(UUID.randomUUID())
				.build();

	//	user.addUserStatus(status);

		UserStatusEntity difId = new UserStatusEntity(status);
		difId.setId(
				BiTemporalEntityId.<UUID>builder()
						.id(UUID.randomUUID())
						.build()
		);

		assertThat(difId).isNotEqualTo(status);
		assertThat(difId.hashCode()).isNotEqualTo(status.hashCode());

	}
}
