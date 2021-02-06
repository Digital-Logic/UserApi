package net.digitallogic.ProjectManager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.persistence.dto.user.LoginRequest;
import net.digitallogic.ProjectManager.web.Routes;
import net.digitallogic.ProjectManager.web.exceptions.MessageCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
				.andExpect(cookie().exists("SESSION"))

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

				.andExpect(cookie().exists("SESSION"))
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



		mockMvc.perform(post(Routes.LOGIN_ROUTE)
			.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest))

		)
				.andExpect(status().isUnauthorized())
				.andExpect(cookie().value(SecurityConstants.ACCESS_TOKEN, blankOrNullString()))
				.andExpect(cookie().value(SecurityConstants.REFRESH_TOKEN, blankOrNullString()))
				;
	}

	@ParameterizedTest
	@MethodSource
	void signInWith_InvalidLogin_Test(LoginRequest request) throws Exception {

		signIn(request)
				.andExpect(status().isUnauthorized())
				.andExpect(cookie().doesNotExist(SecurityConstants.REFRESH_TOKEN))
				.andExpect(cookie().doesNotExist(SecurityConstants.ACCESS_TOKEN))
				.andExpect(jsonPath("$.code", is(MessageCode.AUTH_BAD_CREDENTIALS.code)));

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
}
