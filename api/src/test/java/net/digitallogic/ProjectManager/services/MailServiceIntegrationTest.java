package net.digitallogic.ProjectManager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.events.SendMailEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles(profiles = "non-async")
public class MailServiceIntegrationTest {

	@Autowired
	private MailService mailService;

	RestTemplate client = new RestTemplate();

	private final String baseURL = "http://localhost:8025/api/v2/";

	@Test
	void sendEmailTest() {
		UUID id = UUID.randomUUID();

		mailService.sendEmail(
				SendMailEvent.builder()
						.source(this)
						.templateName("account-activation")
						.fromEmail("noReplay@project-manager.net")
						.recipientEmail("joe@test.com")
						.subject("Test Email")
						.addVariable("name", "Joe Exotic")
						.addVariable("activationLink",
								ServletUriComponentsBuilder.fromCurrentContextPath()
										.queryParam("activate", id.toString())
										.build()
										.toUriString()
						)
						.build()
		);

		JsonNode root = queryClient(id.toString());

		assertThat(root).isNotNull();
		assertThat(root.path("count").intValue()).isEqualTo(1);
	}


	JsonNode queryClient(String value) {

		ResponseEntity<String> response = client.exchange(
				UriComponentsBuilder.fromHttpUrl(baseURL + "search")
						.queryParam("kind", "containing")
						.queryParam("query", value)
						.toUriString(),
				HttpMethod.GET,
				new HttpEntity<>(getHeaders()),
				String.class
		);


		ObjectMapper objectMapper = new ObjectMapper();

		try {
			return objectMapper.readTree(response.getBody());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}


	HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		return headers;
	}
}
