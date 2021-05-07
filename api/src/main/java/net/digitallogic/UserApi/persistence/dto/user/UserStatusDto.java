package net.digitallogic.UserApi.persistence.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.UserApi.persistence.biTemporal.dto.BiTemporalDto;
import net.digitallogic.UserApi.persistence.entity.SoftDelete;
import net.digitallogic.UserApi.persistence.entity.user.UserStatusEntity;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserStatusDto extends BiTemporalDto<UUID> implements SoftDelete {

	private boolean accountEnabled;
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;
	private boolean deleted;

	public UserStatusDto(UserStatusEntity entity) {
		super(entity);
		accountEnabled = entity.isAccountEnabled();
		accountExpired = entity.isAccountExpired();
		accountLocked = entity.isAccountLocked();
		credentialsExpired = entity.isCredentialsExpired();
		deleted = entity.isDeleted();
	}

	public UserStatusDto(UserStatusDto dto) {
		super(dto);
		accountEnabled = dto.isAccountEnabled();
		accountExpired = dto.isAccountExpired();
		accountLocked = dto.isAccountLocked();
		credentialsExpired = dto.isCredentialsExpired();
		deleted = dto.isDeleted();
	}
}
