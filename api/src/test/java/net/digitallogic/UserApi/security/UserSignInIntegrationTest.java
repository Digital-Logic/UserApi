package net.digitallogic.UserApi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.UserApi.persistence.dto.security.LoginRequest;
import net.digitallogic.UserApi.web.Routes;
import net.digitallogic.UserApi.web.error.ErrorCode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserSignInIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();


	@Value("${server.servlet.session.cookie.name}")
	private String sessionCookieName;

	@Value("${rememberMe.cookie.name}")
	private String rememberMeCookieName;

	private final String rememberMeParam="remember-me";

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void signInTest() throws Exception {
		LoginRequest loginRequest = LoginRequest.builder()
				.email("test@testing.com")
				.password("myPassword")
				.build();

		signIn(loginRequest)
				.andExpect(status().isOk())
				// verify session cookie
				.andExpect(cookie().exists(sessionCookieName))
				.andExpect(cookie().doesNotExist(rememberMeCookieName))
				// verify response body.
				.andExpect(jsonPath("$.id", is("4718e879-c061-47bf-bcb4-a2db495b2fe9")))
				.andExpect(jsonPath("$.email", is("test@testing.com")))
				.andExpect(jsonPath("$.authorities", is(empty())))
		;
	}

	@ParameterizedTest(name = "signInRememberMe {0}")
	@ValueSource(strings = {"true", "TRUE", "True", "yes", "YES", "1"})
	@Sql(value = "classpath:db/testUser.sql")
	void signInRememberMeTest(String rememberMeValue) throws Exception {
		LoginRequest loginRequest = LoginRequest.builder()
				.email("test@testing.com")
				.password("myPassword")
				.build();

		signIn(loginRequest, rememberMeValue)
				.andExpect(status().isOk())
				// verify session cookie
				.andExpect(cookie().exists(sessionCookieName))
				.andExpect(cookie().exists(rememberMeCookieName))

				// verify response body.
				.andExpect(jsonPath("$.id", is("4718e879-c061-47bf-bcb4-a2db495b2fe9")))
				.andExpect(jsonPath("$.email", is("test@testing.com")))
				.andExpect(jsonPath("$.authorities", is(empty())))
		;
	}

	@ParameterizedTest(name = "signInRememberMe {0}")
	@ValueSource(strings = {"false", "FALSE", "no", "NO", "tru", "T", "0", "no", "123"})
	@Sql(value = "classpath:db/testUser.sql")
	void signInInvalidRememberMeTest(String rememberMeValue) throws Exception {
		LoginRequest loginRequest = LoginRequest.builder()
				.email("test@testing.com")
				.password("myPassword")
				.build();

		signIn(loginRequest, rememberMeValue)
				.andExpect(status().isOk())
				// verify session cookie
				.andExpect(cookie().exists(sessionCookieName))
				.andExpect(cookie().doesNotExist(rememberMeCookieName))

				// verify response body.
				.andExpect(jsonPath("$.id", is("4718e879-c061-47bf-bcb4-a2db495b2fe9")))
				.andExpect(jsonPath("$.email", is("test@testing.com")))
				.andExpect(jsonPath("$.authorities", is(empty())))
		;
	}

	@Test
	@Sql(value = "classpath:db/adminUser.sql")
	void signInAdminUserTest() throws Exception {
		LoginRequest loginRequest = LoginRequest.builder()
				.email("adminTestUser@gmail.com")
				.password("adminPassword")
				.build();

		signIn(loginRequest)
				.andExpect(status().isOk())

				.andExpect(cookie().exists(sessionCookieName))
				.andExpect(cookie().doesNotExist(rememberMeCookieName))

				// verify response body.
				.andExpect(jsonPath("$.id", is("4876a5ba-319e-4ca1-829d-1f6cb5e3599f")))
				.andExpect(jsonPath("$.email", is("adminTestUser@gmail.com")))
				.andExpect(jsonPath("$.authorities", is(not(empty()))))
		;
	}

	@ParameterizedTest(name = "signInAdminUserRememberMe {0}")
	@ValueSource(strings = {"true", "TRUE", "yes", "YES", "1"})
	@Sql(value = "classpath:db/adminUser.sql")
	void signInAdminUserRememberMeTest(String rememberMeValue) throws Exception {
		LoginRequest loginRequest = LoginRequest.builder()
				.email("adminTestUser@gmail.com")
				.password("adminPassword")
				.build();

		signIn(loginRequest, rememberMeValue)
				.andExpect(status().isOk())
				.andExpect(cookie().exists(sessionCookieName))
				.andExpect(cookie().exists(rememberMeCookieName))
				// verify response body.
				.andExpect(jsonPath("$.id", is("4876a5ba-319e-4ca1-829d-1f6cb5e3599f")))
				.andExpect(jsonPath("$.email", is("adminTestUser@gmail.com")))
				.andExpect(jsonPath("$.authorities", is(not(empty()))))
		;
	}

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void signIn_InvalidLogin_ClearsCookies_Test() throws Exception {
		LoginRequest loginRequest = LoginRequest.builder()
				.email("test@testing.com")
				.password("myPasswords")
				.build();


		signIn(loginRequest, "true")
				.andExpect(status().isUnauthorized())
				.andExpect(cookie().value(rememberMeCookieName, blankOrNullString()))
				.andExpect(cookie().exists(sessionCookieName))
				;
	}

	@Test
	@Disabled
	void postToSignWithNull() throws Exception {
		mockMvc.perform(post(Routes.LOGIN_ROUTE)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
		)
			.andExpect(status().is4xxClientError());
	}

	@MethodSource
	@ParameterizedTest(name = "SignInWith_InvalidLogin {index}")
	void signInWith_InvalidLogin_Test(LoginRequest request) throws Exception {

		signIn(request)
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code", is(HttpStatus.UNAUTHORIZED.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.AUTHENTICATION_FAILURE.code)))
				.andExpect(cookie().value(rememberMeCookieName, blankOrNullString()))
				.andExpect(cookie().exists(sessionCookieName)) // anonymous session
		;

	}

	private static Stream<Arguments> signInWith_InvalidLogin_Test() {
		return Stream.of(
				Arguments.of(
						LoginRequest.builder()
								.email("test@testing.com")
								.password("mypassword")
								.build()),
				Arguments.of(
						LoginRequest.builder()
								.email("tests@testing.com")
								.password("myPassword")
								.build()),
				Arguments.of(
						LoginRequest.builder()
								.email("")
								.password("")
								.build())
		);
	}

	private ResultActions signIn(LoginRequest loginRequest) throws Exception {

		return mockMvc.perform(post(Routes.LOGIN_ROUTE)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest))
		);
	}

	private ResultActions signIn(LoginRequest loginRequest, String rememberMeValue) throws Exception {

		return mockMvc.perform(post(Routes.LOGIN_ROUTE)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.param(rememberMeCookieName, rememberMeValue)
				.content(objectMapper.writeValueAsString(loginRequest))
		);
	}
}
