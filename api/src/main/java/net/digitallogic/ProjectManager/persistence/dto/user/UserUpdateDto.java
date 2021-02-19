package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

	@NotNull(message = "{error.field.notNull.message}")
	private UUID id;

	@Size(min=3, message = "{error.field.size.min.message}")
	private String firstName;

	@Size(min=3, message = "{error.field.size.min.message}")
	private String lastName;

	private boolean deleted;

	@NotNull(message = "{error.field.notNull.message}")
	private int version;

	/* ** Copy constructor ** */
	public UserUpdateDto(UserUpdateDto dto) {
		this.id = dto.getId();
		this.firstName = dto.getFirstName();
		this.lastName = dto.getLastName();
		this.deleted = dto.isDeleted();
		this.version = dto.getVersion();
	}
}
