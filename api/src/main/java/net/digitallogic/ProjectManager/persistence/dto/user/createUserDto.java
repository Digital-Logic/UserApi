package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = {"email"})
public class createUserDto {

	@Size(max = 80)
	@NotNull
	private String email;

	@Size(min=7, max=30)
	@NotNull
	private String password;

	@NotNull
	private String firstName;

	@NotNull
	private String lastName;
}
