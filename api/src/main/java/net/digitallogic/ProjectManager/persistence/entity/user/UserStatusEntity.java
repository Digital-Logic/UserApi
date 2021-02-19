package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.entity.SoftDelete;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "UserStatusEntity")
@Table(name = "user_status")
public class UserStatusEntity extends BiTemporalEntity<UUID> implements SoftDelete {

	@Builder.Default
	@Column(name = "account_enabled", updatable = false)
	private boolean accountEnabled = false;

	@Builder.Default
	@Column(name = "account_expired", updatable = false)
	private boolean accountExpired = false;

	@Builder.Default
	@Column(name = "account_locked", updatable = false)
	private boolean accountLocked = false;

	@Builder.Default
	@Column(name = "credentials_expired", updatable = false)
	private boolean credentialsExpired = false;

	@Builder.Default
	@Column(name = "deleted", updatable = false)
	private boolean deleted = false;

	@MapsId("id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "id")
	private UserEntity user;

	/* ** BiDirectional linking to UserEntity is not maintained
		on coping, This is only intended to be used during testing ** */
	public UserStatusEntity(UserStatusEntity entity) {
		super(entity);
		this.accountEnabled = entity.isAccountEnabled();
		this.accountExpired = entity.isAccountExpired();
		this.accountLocked = entity.isAccountLocked();
		this.credentialsExpired = entity.isCredentialsExpired();
		this.user = entity.getUser();
	}
}
