package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
	private String email;
	private String password;
	private boolean rememberMe;
}
