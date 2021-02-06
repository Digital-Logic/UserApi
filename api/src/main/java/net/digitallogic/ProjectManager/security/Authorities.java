package net.digitallogic.ProjectManager.security;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Authorities {

	ADMIN_USERS("ADMIN_USERS"),
	ADMIN_ROLES("ADMIN_ROLES")
	;

	public final String name;
	Authorities(String name){ this.name = name; }
}
