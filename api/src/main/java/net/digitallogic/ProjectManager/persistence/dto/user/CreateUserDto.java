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

	@Size(max = 60, message = "{error.field.size.max.message}")
	@NotEmpty(message = "{error.field.notNull.message}")
	@Email(message = "{error.field.email.message}")
	private String email;

	@Size(min=7, max=30, message = "{error.field.size.minMax.message}")
	@NotEmpty(message = "{error.field.notNull.message}")
	private String password;

	@NotEmpty(message = "{error.field.notNull.message}")
	@Size(min=3, message = "{error.field.size.min.message}")
	private String firstName;

	@NotEmpty(message = "{error.field.notNull.message}")
	@Size(min=3, message = "{error.field.size.min.message}")
	private String lastName;
}
