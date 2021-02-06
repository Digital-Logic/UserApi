package net.digitallogic.ProjectManager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity_;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.web.Routes;
import net.digitallogic.ProjectManager.web.exceptions.MessageCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
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
				.andExpect(status().isCreated());
	}

	/* ****************************************************** */
	/* **************** Create User Test ******************** */
	/* ****************************************************** */
	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void createDuplicateUserTest() throws Exception {

		CreateUserDto createUser = UserFixtures.createUser();
		createUser.setEmail("test@testing.com");

		doCreateUser(createUser)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(MessageCode.DUPLICATE_ENTITY.code)))
		;
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"asd", "   ", "de       ", "      df", "    asd    "})
	void createUserBadPasswordTest(String password) throws Exception {
		CreateUserDto createUserDto = UserFixtures.createUser();
		createUserDto.setPassword(password);

		doCreateUser(createUserDto)
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("password")))
				.andExpect(jsonPath("$.code", is(MessageCode.FIELD_VALIDATION_ERROR.code)))
		;
	}

	@ParameterizedTest
	@ValueSource(strings = {"jklsdf", "joe@", "   ", ""})
	@NullAndEmptySource
	void createUserBadEmailTest(String email) throws Exception {
		CreateUserDto newUser = UserFixtures.createUser();
		newUser.setEmail(email);

		doCreateUser(newUser)
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("email")))
				.andExpect(jsonPath("$.code", is(MessageCode.FIELD_VALIDATION_ERROR.code)))
		;
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"", "   ", "      ", "sd   ", "   sd"})
	void createUserNoFirstNameTest(String firstName) throws Exception {
		CreateUserDto createUserDto = UserFixtures.createUser();
		createUserDto.setFirstName(firstName);

		doCreateUser(createUserDto)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message.firstName", any(String.class)))
				.andExpect(jsonPath("$.code", is(MessageCode.FIELD_VALIDATION_ERROR.code)))
		;
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"", "    ", "sd   ", "   fe"})
	void createUserNoLastNameTest(String lastName) throws Exception {
		CreateUserDto createUserDto = UserFixtures.createUser();
		createUserDto.setLastName(lastName);

		doCreateUser(createUserDto)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message.lastName", any(String.class)))
				.andExpect(jsonPath("$.code", is(MessageCode.FIELD_VALIDATION_ERROR.code)))
		;

	}

	private ResultActions doCreateUser(CreateUserDto userData) throws Exception {
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
	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"email", "createdDate", "lastModifiedDate", "firstName", "lastName", "lastName, firstName",
			"-last_name", "-first_name", "last_modified_date", "created_date", "last_name, first_name"})
	void getAllUsersSortTest(String sortBy) throws Exception {

		mockMvc.perform(get(Routes.USER_ROUTE)
				.accept(MediaType.APPLICATION_JSON)
				.param("sort", sortBy)
		)
				.andExpect(status().isOk())
		;
	}


	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@ValueSource(strings = {"lestName", "firstName, lastNeme", "Lastname"})
	void getAllUsersInvalidSortTest(String sortBy) throws Exception {

		mockMvc.perform(get(Routes.USER_ROUTE)
				.accept(MediaType.APPLICATION_JSON)
				.param("sort", sortBy)
		)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(MessageCode.ENTITY_INVALID_PROPERTY.code)))
		;
	}

	/* ********************** Test Filtering ******************* */
	@ParameterizedTest
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

	@ParameterizedTest
	@ValueSource(strings = {"firstName==", "firstName=joe", "firstName=like==joe", "firstName<=joe",
			"firstName > joe", "firstName!=sarah"})
	void getAllUsersInvalidFilterTest(String filter) throws Exception {
		doFilterOnUser(filter)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", anyOf(
						equalTo(MessageCode.FILTER_INVALID_QUERY.code),
						equalTo(MessageCode.FILTER_INVALID_COMPARISON_OPERATOR.code)))
				);
		;
	}

	@ParameterizedTest
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
	@ParameterizedTest
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
	void getOneUserInvalidIdTest() throws Exception {
		onGetOneUser("216ca682-8292-4225-89c8-f2b3c9a6ab40", null)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code", is(MessageCode.ENTITY_DOES_NOT_EXIST.code)))
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
	@Sql(value = "classpath:db/multiplyUsers.sql")
	void invalidUserIdPutRequestTest() throws Exception {
		UserUpdateDto update = UserUpdateDto.builder()
				.id(UUID.fromString("4718e879-c061-47bf-bcb4-a2db493b2fe9"))
				.firstName("Thomas")
				.build();

		doUserUpdate(update)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code", is(MessageCode.ENTITY_DOES_NOT_EXIST.code)))

		;
	}

	@ParameterizedTest
	@Sql(value = "classpath:db/multiplyUsers.sql")
	@MethodSource
	void invalidFieldPutRequestTest(UserUpdateDto updateData) throws Exception {
		doUserUpdate(updateData)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(MessageCode.FIELD_VALIDATION_ERROR.code)))
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
