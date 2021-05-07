package net.digitallogic.UserApi.persistence.dto.security;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class LoginRequest {

	@Size(max = 60, message = "{error.field.size.max.message}")
	@NotEmpty
	@Email
	private String email;

	@Size(min=7, max=30, message = "{error.field.size.minMax.message}")
	@NotEmpty
	private String password;
}
