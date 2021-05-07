package net.digitallogic.UserApi.security;

public enum ROLES {

	ADMIN("ADMIN_ROLE"),
	USER("USER_ROLE")
	;

	public final String name;
	ROLES(String name) { this.name = name; }
}
