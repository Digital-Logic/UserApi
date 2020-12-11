package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.entity.AuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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


	public UserEntity(UserDto dto) {
		super(dto);

		this.email = dto.getEmail();
		this.firstName = dto.getFirstName();
		this.lastName = dto.getLastName();
	}
}
