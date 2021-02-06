package net.digitallogic.ProjectManager.security;

import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

@EqualsAndHashCode(of = {"authority"})
public class Authority implements GrantedAuthority {
	private final String authority;

	public Authority(String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return this.authority;
	}
}
