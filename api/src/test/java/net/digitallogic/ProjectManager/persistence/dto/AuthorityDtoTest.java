package net.digitallogic.ProjectManager.persistence.dto;

import net.digitallogic.ProjectManager.fixtures.AuthorityFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.AuthorityDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorityDtoTest {

	@Test
	public void equalsAndHashEqualityTest() {
		AuthorityDto dto = AuthorityFixtures.authorityDto();
		AuthorityDto copy = AuthorityDto.builder().id(dto.getId()).build();

		assertThat(copy).isEqualTo(dto);
		assertThat(copy).hasSameHashCodeAs(dto);
	}

	@Test
	public void equalsAndHashNonEqualityTest() {
		AuthorityDto dto = AuthorityFixtures.authorityDto();
		AuthorityDto copy = new AuthorityDto(dto);
		copy.setId(UUID.randomUUID());

		assertThat(copy).isNotEqualTo(dto);
		assertThat(copy.hashCode()).isNotEqualTo(dto.hashCode());
	}

	@Test
	public void copyConstructorTest() {
		AuthorityDto dto = AuthorityFixtures.authorityDto();
		AuthorityDto copy = new AuthorityDto(dto);
		assertThat(copy).isEqualTo(dto);
		assertThat(copy).hasSameHashCodeAs(dto);

		assertThat(copy).isEqualToComparingFieldByField(dto);
	}
}



