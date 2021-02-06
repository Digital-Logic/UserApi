package net.digitallogic.ProjectManager.security;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(of = {"id"})
@Builder
@Setter
@AllArgsConstructor
@ToString(of = {"id", "email", "accountEnabled", "accountExpired", "accountLocked", "credentialsExpired"})
public class UserAuthentication implements UserDetails {

	private final UUID id;
	@Getter
	private final String email;
	private final String password;

	private final boolean accountEnabled;
	private final boolean accountExpired;
	private final boolean accountLocked;
	private final boolean credentialsExpired;
	private final LocalDateTime validUntil;

	@Singular
	private final Set<Authority> authorities;

	@Override
	public Collection<Authority> getAuthorities() {
		return Collections.unmodifiableSet(authorities);
	}


	public UUID getId() { return id; }

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	@Override
	public boolean isEnabled() {
		return accountEnabled;
	}
}
