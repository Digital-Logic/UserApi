package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.AuditDto;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(of = {"name"}, callSuper = true)
@NoArgsConstructor
public class RoleDto extends AuditDto<UUID> {

	private String name;

	@Builder.Default
	private List<AuthorityDto> authorities = new ArrayList<>();

	public RoleDto(RoleEntity entity) {
		super(entity);
		name = entity.getName();

		if (pu.isLoaded(entity, RoleEntity_.AUTHORITIES)) {
			authorities = entity.getAuthorities()
					.stream()
					.map(AuthorityDto::new)
					.collect(Collectors.toList());
		}
	}

	public RoleDto(RoleDto dto) {
		super(dto);
		name = dto.getName();
		authorities = dto.getAuthorities().stream()
				.map(AuthorityDto::new)
				.collect(Collectors.toList());
	}
}
