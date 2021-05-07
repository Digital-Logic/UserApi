package net.digitallogic.UserApi.services;

import com.fasterxml.jackson.databind.JsonNode;
import net.digitallogic.UserApi.QuerySmtpServer;
import net.digitallogic.UserApi.config.Profiles;
import net.digitallogic.UserApi.events.SendMailEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles(Profiles.NON_ASYNC)
public class NotificationServiceIntegrationTest {

	@Autowired
	private NotificationService notificationService;
	private final QuerySmtpServer server = new QuerySmtpServer();


	@Test
	void sendEmailTest() {
		UUID id = UUID.randomUUID();

		notificationService.sendNotification(
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

		JsonNode root = server.queryClient(id.toString());

		assertThat(root).isNotNull();
		assertThat(root.path("count").intValue()).isEqualTo(1);
	}
}
