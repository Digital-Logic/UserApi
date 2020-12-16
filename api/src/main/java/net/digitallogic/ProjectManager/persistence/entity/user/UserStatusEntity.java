package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@SuperBuilder
@Entity(name = "UserStatusEntity")
@Table(name = "user_status")
public class UserStatusEntity extends BiTemporalEntity<UUID> {

	@Builder.Default
	@Column(name = "account_enabled")
	private boolean accountEnabled = false;

	@Builder.Default
	@Column(name = "account_expired")
	private boolean accountExpired = false;

	@Builder.Default
	@Column(name = "account_locked")
	private boolean accountLocked = false;

	@Builder.Default
	@Column(name = "credentials_expired")
	private boolean credentialsExpired = false;

	@MapsId("id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "id")
	private UserEntity user;
}
