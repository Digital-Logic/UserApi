package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.biTemporal.dto.BiTemporalDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserStatusDto extends BiTemporalDto<UUID> {

	private boolean accountEnabled;
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;

	public UserStatusDto(UserStatusEntity entity) {
		super(entity);
		accountEnabled = entity.isAccountEnabled();
		accountExpired = entity.isAccountExpired();
		accountLocked = entity.isAccountLocked();
		credentialsExpired = entity.isCredentialsExpired();
	}

	public UserStatusDto(UserStatusDto dto) {
		super(dto);
		accountEnabled = dto.isAccountEnabled();
		accountExpired = dto.isAccountExpired();
		accountLocked = dto.isAccountLocked();
		credentialsExpired = dto.isCredentialsExpired();
	}
}
