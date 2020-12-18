package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.AuditEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "id.id",
			cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private Set<UserStatusEntity> userStatus = new HashSet<>();

	public void addRole(RoleEntity role) {
		roles.add(role);
	}
	public void removeRole(RoleEntity role) {
		roles.remove(role);
	}

	public void addUserStatus(UserStatusEntity userStatusEntity) {
		this.userStatus.add(userStatusEntity);
		userStatusEntity.setUser(this);
		userStatusEntity.getId().setId(this.getId());
	}

	public UserEntity(UserEntity entity) {
		super(entity);
		this.email = entity.getEmail();
		this.password = entity.getPassword();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();

		//Deep copy roles
		this.roles = entity.getRoles().stream()
				.map(RoleEntity::new)
				.collect(Collectors.toSet());

		//copy user status
		this.userStatus = entity.getUserStatus().stream()
				.map(UserStatusEntity::new)
				.collect(Collectors.toSet());
	}
}
