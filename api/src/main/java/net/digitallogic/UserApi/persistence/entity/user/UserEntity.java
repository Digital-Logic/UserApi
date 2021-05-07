package net.digitallogic.UserApi.persistence.entity.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.UserApi.persistence.entity.EntityBase;
import net.digitallogic.UserApi.persistence.entity.auth.RoleEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("CommentedOutCode")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(of = {"email"}, callSuper = true)
@Entity(name = "UserEntity")
@Table(name = "user_entity")
public class UserEntity extends EntityBase<UUID> {

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
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "id.id")
	private Set<UserStatusEntity> userStatus = new HashSet<>();

	public void addRole(RoleEntity role) {
		roles.add(role);
	}
	public void removeRole(RoleEntity role) {
		roles.remove(role);
	}

//	public void addUserStatus(UserStatusEntity userStatusEntity) {
//		this.userStatus.add(userStatusEntity);
//		userStatusEntity.setUser(this);
//	}
//
//	public void addUserStatus(UserStatusEntity status, LocalDateTime effective) {
//		status.setId(
//				BiTemporalEntityId.<UUID>builder()
//						.id(this.getId())
//						.validStart(effective)
//						.build()
//		);
//		this.userStatus.add(status);
//	}

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

		//Deep copy status
		this.userStatus = entity.getUserStatus().stream()
				.map(UserStatusEntity::new)
				.collect(Collectors.toSet());
	}
}
