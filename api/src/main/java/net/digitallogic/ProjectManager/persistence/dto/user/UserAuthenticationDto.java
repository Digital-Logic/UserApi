package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.digitallogic.ProjectManager.security.Authority;
import net.digitallogic.ProjectManager.security.UserAuthentication;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class UserAuthenticationDto {
	private final UUID id;
	private final String email;
	private final Set<String> authorities;

	public UserAuthenticationDto(UserAuthentication auth) {
		this.id = auth.getId();
		this.email = auth.getEmail();
		this.authorities = auth.getAuthorities().stream()
				.map(Authority::getAuthority)
				.collect(Collectors.toSet());
	}
}
