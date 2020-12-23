package net.digitallogic.ProjectManager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.web.Routes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager entityManager;

	private ObjectMapper mapper = new ObjectMapper();


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
	@Sql(value="classpath:db/testUser.sql")
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

		Stream.of(null, "", "asd").forEach(password -> {
			try {
				createUserDto.setPassword(password);
				mockMvc.perform(post(Routes.USER_ROUTE)
						.content(mapper.writeValueAsString(createUserDto))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
				)
						.andExpect(status().isBadRequest());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	@Test
	void createUserBadEmailTest() throws Exception {
		CreateUserDto createUserDto = UserFixtures.createUser();

		Stream.of("jklsdf", "joe@", null).forEach(email -> {
			try {
				createUserDto.setEmail("jlskdf");
				mockMvc.perform(post(Routes.USER_ROUTE)
						.content(mapper.writeValueAsString(createUserDto))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
				)
						.andExpect(status().isBadRequest());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
}
