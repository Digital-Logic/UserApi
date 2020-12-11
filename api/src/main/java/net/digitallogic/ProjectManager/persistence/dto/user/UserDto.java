package net.digitallogic.ProjectManager.persistence.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.AuditDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto extends AuditDto<UUID> {

	private String email;
	private String firstName;
	private String lastName;


	public UserDto(UserEntity entity) {
		super(entity);
		this.email = entity.getEmail();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();
	}
}
