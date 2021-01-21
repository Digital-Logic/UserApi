package net.digitallogic.ProjectManager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.web.Routes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.fail;
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

	@Test
	@Sql(value = "classpath:db/testUser.sql")
	void createDuplicateUserTest() throws Exception {

		CreateUserDto createUser = UserFixtures.createUser();
		createUser.setEmail("test@testing.com");

		mockMvc.perform(post(Routes.USER_ROUTE)
				.content(mapper.writeValueAsString(createUser))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		)
				.andExpect(status().isBadRequest());
	}

	@Test
	void createUserBadPasswordTest() {
		CreateUserDto createUserDto = UserFixtures.createUser();

		Stream.of(null, "", "asd", "        ", "de          ", "        df", "   sdfjkl   ")
				.forEach(password -> {
					try {
						createUserDto.setPassword(password);
						mockMvc.perform(post(Routes.USER_ROUTE)
								.content(mapper.writeValueAsString(createUserDto))
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
						)
								.andExpect(status().isBadRequest())
								.andExpect(content().string(containsString("password")))
						;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
	}

	@Test
	void createUserBadEmailTest() {
		CreateUserDto createUserDto = UserFixtures.createUser();

		Stream.of("jklsdf", "joe@", null, "   ", "")
				.forEach(email -> {
					try {
						createUserDto.setEmail("jlskdf");
						mockMvc.perform(post(Routes.USER_ROUTE)
								.content(mapper.writeValueAsString(createUserDto))
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
						)
								.andExpect(status().isBadRequest())
								.andExpect(content().string(containsString("email")))
						;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
	}

	@Test
	void createUserNoFirstNameTest() {
		CreateUserDto createUserDto = UserFixtures.createUser();

		Stream.of("", null, "    ", "       ")
				.forEach(firstName -> {
					try {
						createUserDto.setFirstName(firstName);
						mockMvc.perform(post(Routes.USER_ROUTE)
								.content(mapper.writeValueAsString(createUserDto))
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
						)
								.andExpect(status().isBadRequest())
								.andExpect(content().string(containsString("firstName")))
						;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
	}

	@Test
	void createUserNoLastNameTest() {
		CreateUserDto createUserDto = UserFixtures.createUser();

		Stream.of("", null, "      ", "          ")
				.forEach(lastname -> {
					try {
						createUserDto.setLastName(lastname);
						mockMvc.perform(post(Routes.USER_ROUTE)
								.content(mapper.writeValueAsString(createUserDto))
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
						)
								.andExpect(status().isBadRequest())
								.andExpect(content().string(containsString("lastName")))
						;
					} catch (Exception ex) {
						fail("Exception thrown in test: createUserNoLastnameTest: " + ex.getMessage());
					}
				});
	}

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

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	void getAllUsersSortTest() {
		Stream.of("email", "createdDate", "lastModifiedDate", "firstName", "lastName", "lastName, firstName",
				"last_name", "first_name", "last_modified_date", "created_date", "last_name, first_name")
				.forEach(sortBy -> {
					try {
						mockMvc.perform(get(Routes.USER_ROUTE)
								.accept(MediaType.APPLICATION_JSON)
								.param("sort", sortBy)
						)
								.andExpect(status().isOk())
						;
					} catch (Exception ex) {
						fail("Exception thrown in test: getAllUsersSortTest: " + ex.getMessage());
					}
				});
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	void getAllUsersInvalidSortTest() {
		Stream.of("lestName", "firstName, lastNeme")
				.forEach(sortBy -> {
					try {
						mockMvc.perform(get(Routes.USER_ROUTE)
								.accept(MediaType.APPLICATION_JSON)
								.param("sort", sortBy)
						)
								.andExpect(status().isBadRequest())
						;
					} catch (Exception ex) {
						fail("Exception thrown in test: getAllUsersInvalidSortTest: " + ex.getMessage());
					}
				});
	}

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

		mockMvc.perform(put(Routes.USER_ROUTE + "/" + user.getId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(updateData))
		)
				.andExpect(status().isOk());
	}

	@Test
	@Sql(value = "classpath:db/multiplyUsers.sql")
	void invalidPutRequestTest() {
		UserEntity user = userRepository.findByEmail("joe@exotic.net")
				.orElseThrow();

		List<UserUpdateDto> updateData = List.of(
				UserUpdateDto.builder()
						.lastName("Dirt")
						.version(user.getVersion())
						.build()
		);

		updateData.forEach(data -> {
			try {
				mockMvc.perform(put(Routes.USER_ROUTE + "/" + user.getId())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(data))
				)
						.andExpect(status().isBadRequest());

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
