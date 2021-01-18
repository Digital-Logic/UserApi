package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = {"email"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

	@Size(max = 60, message = "{error.field.Size.max.message}")
	@NotEmpty(message = "{error.field.NotNull.message}")
	@Email(message = "{error.field.Email.message}")
	private String email;

	@Size(min=7, max=30, message = "{error.field.Size.minMax.message}")
	@NotEmpty(message = "{error.field.NotNull.message}")
	private String password;

	@NotEmpty(message = "{error.field.NotNull.message}")
	@Size(min=3)
	private String firstName;

	@NotEmpty(message = "{error.field.NotNull.message}")
	@Size(min=3)
	private String lastName;
}
