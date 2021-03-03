package net.digitallogic.ProjectManager.persistence.entity.auth;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.AuditEntity;
import net.digitallogic.ProjectManager.persistence.entity.SoftDelete;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(of = {"name"}, callSuper = true)
@Entity(name = "RoleEntity")
@Table(name = "role_entity")
public class RoleEntity extends AuditEntity<UUID> implements SoftDelete {

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "role_authority_lookup",
			joinColumns = @JoinColumn(name = "role_id"),
			inverseJoinColumns = @JoinColumn(name = "authority_id")
	)
	@Builder.Default
	private Set<AuthorityEntity> authorities = new HashSet<>();

	public void addAuthority(AuthorityEntity authority) {
		authorities.add(authority);
	}

	public void removeAuthority(AuthorityEntity authority) {
		authorities.remove(authority);
	}


	public RoleEntity(RoleEntity entity) {
		super(entity);
		this.name = entity.getName();

		// Copy authorities
		this.authorities = entity.getAuthorities().stream()
				.map(AuthorityEntity::new)
				.collect(Collectors.toSet());
	}
}
