package net.digitallogic.ProjectManager.persistence.dto;

import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.UserStatusDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatusDtoTest {

	private UserStatusDto get() {
		UserEntity user = UserFixtures.userEntity();
		UserStatusEntity status = (UserStatusEntity) UserStatusEntity.builder()
				.accountEnabled(true)
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.createdBy(UUID.randomUUID())
				.build();
		user.addUserStatus(status);

		return new UserStatusDto(status);
	}

	@Test
	public void equalsAndHashEqualityTest() {
		UserStatusDto status = get();
		UserStatusDto copy = new UserStatusDto();

		copy.setId(status.getId());
		copy.setValidStart(status.getValidStart());
		copy.setSystemStart(status.getSystemStart());

		assertThat(copy).isEqualTo(status);
		assertThat(copy).hasSameHashCodeAs(status);
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		final UserStatusDto dto = get();

		Stream.of("id", "validStart", "systemStart")
				.forEach(fieldName -> {
					UserStatusDto copy = new UserStatusDto(dto);
					try {
						if (fieldName.equals("id"))
							PropertyUtils.setProperty(copy, fieldName, UUID.randomUUID());
						else PropertyUtils.setProperty(copy, fieldName, LocalDateTime.now());

						assertThat(copy).isNotEqualTo(dto);
						assertThat(copy.hashCode()).isNotEqualTo(dto.hashCode());
					}catch (Exception ex) {
						ex.printStackTrace();
						throw new RuntimeException(ex);
					}
				});
	}

	@Test
	public void copyConstructorTest() {
		UserStatusDto status = get();
		UserStatusDto copy = new UserStatusDto(status);

		assertThat(copy).isEqualToComparingFieldByField(status);
	}
}
