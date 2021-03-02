package net.digitallogic.ProjectManager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserRequest;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity_;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.web.Routes;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	private final ObjectMapper mapper = new ObjectMapper();


	@Test
	void createUserTest() throws Exception {

		mockMvc.perform(post(Routes.USER_ROUTE)
				// Add createUser to post body
				.content(mapper.writeValueAsString(UserFixtures.createUser()))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		)
				.andExpect(status().isCreated()) // TODO add more test when I know how this method will respond
		;
	}

	/* ****************************************************** */
	/* **************** Create User Test ******************** */
	/* ****************************************************** */
	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void createDuplicateUserTest() throws Exception {

		CreateUserRequest createUser = UserFixtures.createUser();
		createUser.setEmail("test@testing.com");

		doCreateUser(createUser)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.DUPLICATE_ENTITY.code)))
				.andExpect(jsonPath("$.message", containsString("test@testing.com")))
		;
	}

	@ParameterizedTest(name = "createUserBadPassword {0}")
	@NullAndEmptySource
	@ValueSource(strings = {"asd", "   ", "de       ", "      df", "    asd    "})
	void createUserBadPasswordTest(String password) throws Exception {
		CreateUserRequest createUserRequest = UserFixtures.createUser();
		createUserRequest.setPassword(password);

		doCreateUser(createUserRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.VALIDATION_FAILED.code)))
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$.errors[*].domain", hasItem("password")))
				.andExpect(jsonPath("$.errors[*].reason", hasItem(ErrorCode.VALIDATION_FAILED.code)))
				.andExpect(jsonPath("$.errors[*].message", hasItem(any(String.class))))
		;
	}

	@ParameterizedTest(name = "createUserBadEmail {0}")
	@ValueSource(strings = {"jklsdf", "joe@", "   ", ""})
	@NullAndEmptySource
	void createUserBadEmailTest(String email) throws Exception {
		CreateUserRequest newUser = UserFixtures.createUser();
		newUser.setEmail(email);

		doCreateUser(newUser)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.VALIDATION_FAILED.code)))
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$.errors[*].domain", hasItem("email")))
				.andExpect(jsonPath("$.errors[*].reason", hasItem(ErrorCode.VALIDATION_FAILED.code)))
				.andExpect(jsonPath("$.errors[*].message", hasItem(any(String.class))))
		;
	}

	@ParameterizedTest(name = "createUserNoFirstName {0}")
	@NullAndEmptySource
	@ValueSource(strings = {"", "   ", "      ", "sd   ", "   sd"})
	void createUserNoFirstNameTest(String firstName) throws Exception {
		CreateUserRequest createUserRequest = UserFixtures.createUser();
		createUserRequest.setFirstName(firstName);

		doCreateUser(createUserRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(400)))
				.andExpect(jsonPath("$.reason", is("ValidationFailed")))
				.andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$.errors[*].domain", hasItem("firstName")))
				.andExpect(jsonPath("$.errors[*].reason", hasItem("ValidationFailed")))
				.andExpect(jsonPath("$.errors[*].message", hasItem(any(String.class))))
		;
	}

	@ParameterizedTest(name = "createUserNoLastName {0}")
	@NullAndEmptySource
	@ValueSource(strings = {"", "    ", "sd   ", "   fe"})
	void createUserNoLastNameTest(String lastName) throws Exception {
		CreateUserRequest createUserRequest = UserFixtures.createUser();
		createUserRequest.setLastName(lastName);

		doCreateUser(createUserRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.reason", is("ValidationFailed")))
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors[*].domain", hasItem("lastName")))
				.andExpect(jsonPath("$.errors[*].reason", hasItem("ValidationFailed")))
				.andExpect(jsonPath("$.errors[*].message", hasItem(any(String.class))))
		;

	}

	private ResultActions doCreateUser(CreateUserRequest userData) throws Exception {
		return mockMvc.perform(post(Routes.USER_ROUTE)
				.content(mapper.writeValueAsString(userData))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		);
	}

	/* ****************************************************** */
	/* ***************** Get All Users Test ***************** */
	/* ****************************************************** */
	@Test
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@Sql(value = "classpath:db/multiplyUsers.sql")
	void getAllUsersTest() throws Exception {
		mockMvc.perform(get(Routes.USER_ROUTE)
				.accept(MediaType.APPLICATION_JSON)
		)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
		;
	}


	/* ******************** Test Sort ******************** */
	@ParameterizedTest(name = "SortUsers by {0}")
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"email", "createdDate", "lastModifiedDate", "firstName", "lastName", "lastName, firstName",
			"-last_name", "-first_name", "last_modified_date", "created_date", "last_name, first_name"})
	void getAllUsersSortTest(String sortBy) throws Exception {

		mockMvc.perform(get(Routes.USER_ROUTE)
				.accept(MediaType.APPLICATION_JSON)
				.param("sort", sortBy)
		)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
		;
	}


	@ParameterizedTest(name = "InvalidSort by {0}")
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"lestName", "firstName, lastNeme", "Lastname"})
	void getAllUsersInvalidSortTest(String sortBy) throws Exception {

		mockMvc.perform(get(Routes.USER_ROUTE)
				.accept(MediaType.APPLICATION_JSON)
				.param("sort", sortBy)
		)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.INVALID_PROPERTY.code)))
				.andExpect(jsonPath("$.message", is(any(String.class))))
		;
	}

	/* ********************** Test Filtering ******************* */
	@ParameterizedTest(name = "FirstNameFilter with {0}")
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"firstName==Sarah", "firstName==  Sarah ", " first_name == Sarah  ", "firstName==Sarah", "first_Name==Sarah",
			"firstName=like=Sara_", "firstName=like=_arah", "firstName=like=Sarah", "firstName=like=Sa*",
			"firstName=ilike=sara_", "firstName=ilike= sara_ ", "first_name=ilike=_araH", "first_name=ilike= _araH"})
	void getAllUsersFirstNameFilterTest(String filter) throws Exception {
		doFilterOnUser(filter)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].firstName", is("Sarah")))
		;
	}

	@ParameterizedTest(name = "InvalidFilter with {0}")
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@ValueSource(strings = {"firstName==", "firstName=joe", "firstName=like==joe", "firstName<=joe",
			"firstName > joe", "firstName!=sarah"})
	void getAllUsersInvalidFilterTest(String filter) throws Exception {
		doFilterOnUser(filter)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.reason", anyOf(
						equalTo(ErrorCode.INVALID_FILTER_QUERY.code),
						equalTo(ErrorCode.CONVERSION_FAILED.code)
				)))
		;
	}

	@ParameterizedTest(name = "LastNameFilter by {0}")
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"lastName==Exotic", "last_name==Exotic",
			"lastName=like=Exotic", "lastName=like=Exoti_", "lastName=like= _xotic", "lastName=like=Exot*", "lastName=like=*otic",
			"lastName=ilike=Exotic", "lastName=ilike=Exoti_", "lastName=ilike= _xotic", "lastName=ilike=Exot*", "lastName=ilike=*otic",})
	void getAllUsersLastNameFilterTest(String filter) throws Exception {
		doFilterOnUser(filter)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].lastName", is("Exotic")))
		;
	}


	private ResultActions doFilterOnUser(String filter) throws Exception {
		return mockMvc.perform(get(Routes.USER_ROUTE)
				.accept(MediaType.APPLICATION_JSON)
				.param("filter", filter)
		);
	}

	/* ****************************************************** */
	/* ****************** Get One User Test ***************** */
	/* ****************************************************** */
	@ParameterizedTest(name = "Expansion by {0}")
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@Sql(value = "classpath:db/testUser.sql")
	@ValueSource(strings = {UserEntity_.ROLES, RoleEntity_.AUTHORITIES, UserEntity_.ROLES + "," + RoleEntity_.AUTHORITIES})
	@NullAndEmptySource
	void getOneUserWithExpandPropsTest(String expand) throws Exception {
		onGetOneUser("4718e879-c061-47bf-bcb4-a2db495b2fe9", expand)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is("4718e879-c061-47bf-bcb4-a2db495b2fe9")))
		;
	}

	@Test
	@WithMockUser(authorities = {"ADMIN_USERS"})
	void getOneUserInvalidIdTest() throws Exception {
		onGetOneUser("216ca682-8292-4225-89c8-f2b3c9a6ab40", null)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.NON_EXISTENT_ENTITY.code)))
				.andExpect(jsonPath("$.message", containsString("216ca682-8292-4225-89c8-f2b3c9a6ab40")))
		;
	}

	private ResultActions onGetOneUser(String id, @Nullable String expand) throws Exception {
		return mockMvc.perform(get(Routes.USER_ROUTE + "/" + id)
				.accept(MediaType.APPLICATION_JSON)
				.param("expand", expand)
		);
	}


	/* ****************************************************** */
	/* **************** Update User Test ******************** */
	/* ****************************************************** */
	@Test
	@WithMockUser
	@Sql(value = "classpath:db/multiplyUsers.sql")
	void putRequestTest() throws Exception {
		UserEntity user = userRepository.findByEmail("joe@exotic.net")
				.orElseThrow();

		UserUpdateDto updateData = UserUpdateDto.builder()
				.id(user.getId())
				.lastName("Dirt")
				.version(user.getVersion())
				.build();

		doUserUpdate(updateData)
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	@Sql(value = "classpath:db/multiplyUsers.sql")
	void invalidUserIdPutRequestTest() throws Exception {
		UserUpdateDto update = UserUpdateDto.builder()
				.id(UUID.fromString("4718e879-c061-47bf-bcb4-a2db493b2fe9"))
				.firstName("Thomas")
				.build();

		doUserUpdate(update)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.NON_EXISTENT_ENTITY.code)))
				.andExpect(jsonPath("message", is(any(String.class))))
		;
	}

	@ParameterizedTest(name = "InvalidPutRequest {index}")
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@WithMockUser(authorities = {"ADMIN_USERS"})
	@MethodSource
	void invalidFieldPutRequestTest(UserUpdateDto updateData) throws Exception {
		doUserUpdate(updateData)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.reason", is(ErrorCode.VALIDATION_FAILED.code)))
				.andExpect(jsonPath("$.errors").exists())
		;

	}

	private static Stream<Arguments> invalidFieldPutRequestTest() {
		UUID id = UUID.fromString("99c9c540-5f07-442e-9964-7da9e911f3a5");

		return Stream.of(
				Arguments.of(
						UserUpdateDto.builder()
								.id(id)
								.lastName("Dr")
								.build()),
				Arguments.of(
						UserUpdateDto.builder()
								.id(id)
								.firstName("R")
								.build())
		);
	}

	private ResultActions doUserUpdate(UserUpdateDto updateData) throws Exception {
		return mockMvc.perform(put(Routes.USER_ROUTE + "/" + updateData.getId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(updateData))
		);
	}
}
