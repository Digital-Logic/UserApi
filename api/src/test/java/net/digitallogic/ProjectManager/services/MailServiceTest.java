package net.digitallogic.ProjectManager.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

public class MailServiceTest {

	@Mock
	JavaMailSender mailSender;

	@Mock
	ITemplateEngine templateEngine;

	@InjectMocks
	MailServiceImpl mailService;

	AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void teardown() throws Exception {
		closeable.close();
	}

	@Test
	void sendMailTest() {

//		when(templateEngine.process(anyString(), any(Context.class)))
//				.thenReturn("Message body text.");
//
//		doNothing().when(mailSender).send(any(MimeMessage.class));
//
//		when(mailSender.createMimeMessage())
//				.thenReturn()
//
//		mailService.sendEmail(SendMailEvent.builder()
//				.source(this)
//				.recipientEmail("somewhere@gmail.com")
//				.fromEmail("noReply@gmail.com")
//				.subject("Something")
//				.templateName("my-template")
//		.build());
	}
}
