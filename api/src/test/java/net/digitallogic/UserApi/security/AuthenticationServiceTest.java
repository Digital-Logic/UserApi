package net.digitallogic.UserApi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class AuthenticationServiceTest {

	@Autowired
	AuthenticationService authenticationService;

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	void loadAdminUserTest() {

		UserAuthentication auth = authenticationService.loadUserByUsername("adminTestUser@gmail.com");
		assertThat(auth).isNotNull();
		assertThat(auth.getId()).isEqualTo(UUID.fromString("4876a5ba-319e-4ca1-829d-1f6cb5e3599f"));
		assertThat(auth.getAuthorities())
				.extracting(Authority::getAuthority)
				.containsOnly(Authorities.ADMIN_USERS.name, Authorities.ADMIN_ROLES.name);
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void loadUserTest() {
		UserAuthentication auth = authenticationService.loadUserByUsername("test@testing.com");
		assertThat(auth).isNotNull();
		assertThat(auth.getAuthorities()).hasSize(0);
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void loadInvalidUserTest() {

		assertThatThrownBy(() ->
				authenticationService.loadUserByUsername("teste@testing.com")
		).isInstanceOf(UsernameNotFoundException.class);
	}
}
