package net.digitallogic.UserApi.persistence.entity.user;

import net.digitallogic.UserApi.fixtures.UserFixtures;
import net.digitallogic.UserApi.persistence.biTemporal.entity.BiTemporalEntityId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatusEntityTest {

	@Test
	public void equalsAndHashEqualityTest() {
		UserEntity user = UserFixtures.userEntity();
		UserStatusEntity status = UserStatusEntity.builder()
				.id(user.getId())
				.user(user)
				.accountEnabled(true)
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.createdBy(UUID.randomUUID())
				.build();

		UserStatusEntity copy = UserStatusEntity.builder()
				//.id(new BiTemporalEntityId<>(user.getId(), status.getValidStart()))
				.id(user.getId())
				.build();

		assertThat(copy).isEqualTo(status);
		assertThat(copy).hasSameHashCodeAs(status);
	}

	// TODO fix user.addUserStatus
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

	@Test
	public void builderTest() {
		UserEntity user = UserFixtures.userEntity();
		LocalDateTime now = LocalDateTime.now();

		UserStatusEntity entity = UserStatusEntity.builder()
				.id(user.getId())
				.validStart(now)
				.systemStart(now)
				.build();

		assertThat(entity.getId()).isNotNull();
		assertThat(entity.getSystemStart()).isEqualTo(now);
		assertThat(entity.getValidStart()).isEqualTo(now);
	}

	@Test
	public void builderTest2() {
		UserEntity user = UserFixtures.userEntity();
		LocalDateTime now = LocalDateTime.now();

		UserStatusEntity status = UserStatusEntity.builder()
				.user(user)
				.validStart(now)
				.systemStart(now)
				.accountEnabled(true)
				.accountExpired(true)
				.accountLocked(true)
				.credentialsExpired(true)
				.build();

		assertThat(status.isAccountEnabled()).isTrue();
		assertThat(status.isAccountExpired()).isTrue();
		assertThat(status.isAccountLocked()).isTrue();
		assertThat(status.isCredentialsExpired()).isTrue();
		assertThat(status.getEmbeddedId()).isNull();
		assertThat(status.getUser()).isNotNull();
	}
}
