package net.digitallogic.UserApi.persistence.dto;

import net.digitallogic.UserApi.fixtures.RoleFixtures;
import net.digitallogic.UserApi.persistence.dto.auth.RoleDto;
import net.digitallogic.UserApi.persistence.entity.auth.RoleEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleDtoTest {

	@Test
	public void equalsAndHashEqualityTest() {
		RoleDto dto = RoleFixtures.roleDto();
		RoleDto copy = RoleDto.builder()
				.id(dto.getId())
				.build();

		assertThat(copy).hasSameHashCodeAs(dto);
		assertThat(copy).isEqualTo(dto);
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		RoleDto dto = RoleFixtures.roleDto();

		RoleDto copy = new RoleDto(dto);
		assertThat(copy).isEqualTo(dto);
		assertThat(copy).isEqualToComparingFieldByField(dto);

		copy.setId(UUID.randomUUID());

		assertThat(copy).isNotEqualTo(dto);
		assertThat(copy.hashCode()).isNotEqualTo(dto.hashCode());
	}

	@Test
	public void copyConstructorTest() {
		RoleDto dto = RoleFixtures.roleDto();
		RoleDto copy = new RoleDto(dto);
		assertThat(copy).isEqualTo(dto);
		assertThat(copy).isEqualToComparingFieldByField(dto);
	}

	@Test
	public void mapEntityToDtoTest() {
		RoleEntity entity = RoleFixtures.roleEntity();
		RoleDto dto = new RoleDto(entity);

		assertThat(dto).isEqualToComparingOnlyGivenFields(entity,
				"id", "name", "version",
				"deleted", "createdDate", "createdBy", "lastModifiedDate",
				"lastModifiedBy");

		assertThat(dto.getAuthorities()).hasSameSizeAs(entity.getAuthorities());
	}
}
