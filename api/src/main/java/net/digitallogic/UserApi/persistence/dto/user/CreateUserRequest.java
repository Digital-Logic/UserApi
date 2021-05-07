package net.digitallogic.UserApi.persistence.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = {"email"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

	@Size(max = 60, message = "{error.field.size.max.message}")
	@NotEmpty
	@Email
	private String email;

	@Size(min=7, max=30, message = "{error.field.size.minMax.message}")
	@NotEmpty
	private String password;

	@NotEmpty
	@Size(min=3, message = "{error.field.size.min.message}")
	private String firstName;

	@NotEmpty
	@Size(min=3, message = "{error.field.size.min.message}")
	private String lastName;
}
