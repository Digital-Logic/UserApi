package net.digitallogic.ProjectManager.persistence.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.AuditDto;
import net.digitallogic.ProjectManager.persistence.entity.SoftDelete;
import net.digitallogic.ProjectManager.persistence.entity.auth.RoleEntity;
import net.digitallogic.ProjectManager.persistence.entity.auth.RoleEntity_;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(of = {"name"}, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RoleDto extends AuditDto<UUID> implements SoftDelete {

	private String name;

	@Builder.Default
	private List<AuthorityDto> authorities = new ArrayList<>();

	private boolean deleted;

	public RoleDto(RoleEntity entity) {
		super(entity);
		name = entity.getName();
		deleted = entity.isDeleted();

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
