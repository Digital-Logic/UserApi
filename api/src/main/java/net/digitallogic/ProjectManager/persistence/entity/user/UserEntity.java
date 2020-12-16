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
@ToString(of = {"email"}, callSuper = true)
@Entity(name = "UserEntity")
@Table(name = "user_entity")
public class UserEntity extends AuditEntity<UUID> {

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "user_role_lookup",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<RoleEntity> roles = new HashSet<>();

	public void addRole(RoleEntity role) {
		roles.add(role);
	}

	public void removeRole(RoleEntity role) {
		roles.remove(role);
	}


//	public UserEntity(UserDto dto) {
//		super(dto);
//
//		this.email = dto.getEmail();
//		this.firstName = dto.getFirstName();
//		this.lastName = dto.getLastName();
//	}
}
