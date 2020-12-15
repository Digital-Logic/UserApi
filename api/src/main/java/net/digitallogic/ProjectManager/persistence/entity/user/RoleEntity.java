package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.AuditEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(of = {"name"}, callSuper = true)
@Entity(name = "RoleEntity")
@Table(name = "role_entity")
public class RoleEntity extends AuditEntity<UUID> {

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@ManyToMany
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
}
