package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = {"email"})
@Builder
public class CreateUserDto {

	@Size(max = 60, message = "{error.field.Size.max.message}")
	@NotNull(message = "{error.field.NotNull.message}")
	@Email(message = "{error.field.Email.message}")
	private String email;

	@Size(min=7, max=30, message = "{error.field.Size.minMax.message}")
	@NotNull(message = "{error.field.NotNull.message}")
	private String password;

	@NotNull(message = "{error.field.NotNull.message}")
	private String firstName;

	@NotNull(message = "{error.field.NotNull.message}")
	private String lastName;
}
