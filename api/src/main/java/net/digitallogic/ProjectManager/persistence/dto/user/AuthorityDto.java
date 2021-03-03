package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.*;
import net.digitallogic.ProjectManager.persistence.entity.auth.AuthorityEntity;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {

	private UUID id;
	private String name;

	public AuthorityDto(AuthorityEntity entity) {
		this.id = entity.getId();
		this.name = entity.getName();
	}

	public AuthorityDto(AuthorityDto dto) {
		this.id = dto.getId();
		this.name = dto.getName();
	}
}
