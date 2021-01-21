package net.digitallogic.ProjectManager.security;

public enum AUTHORITIES {

	ADMIN_USERS("ADMIN_USERS"),
	ADMIN_ROLES("ADMIN_ROLES")
	;

	public final String name;
	AUTHORITIES(String name){ this.name = name; }
}
