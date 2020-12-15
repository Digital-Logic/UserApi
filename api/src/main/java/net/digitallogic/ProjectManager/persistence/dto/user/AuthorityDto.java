package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.digitallogic.ProjectManager.persistence.entity.user.AuthorityEntity;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class AuthorityDto {

	private UUID id;
	private String name;


	public AuthorityDto(AuthorityEntity entity) {
		this.id = entity.getId();
		this.name = entity.getName();
	}
}
