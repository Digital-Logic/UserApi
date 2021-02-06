package net.digitallogic.ProjectManager.web;

public class Routes {

	public static final String API = "/api";
	public static final String USER = "/user";
	public static final String ROLE = "/role";
	public static final String AUTHORITIES = "/authorities";
	public static final String AUTH = "/auth";

	public static final String USER_ROUTE = API + USER;
	public static final String ROLE_ROUTE = API + ROLE;
	public static final String AUTH_ROUTE = API + AUTH;

	public static final String LOGIN_ROUTE = AUTH_ROUTE + "/login";
	public static final String LOGOUT_ROUTE = AUTH_ROUTE + "/logout";
	public static final String SIGN_UP_ROUTE = USER_ROUTE;
}
