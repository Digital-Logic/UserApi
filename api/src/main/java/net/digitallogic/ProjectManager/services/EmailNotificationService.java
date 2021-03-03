package net.digitallogic.ProjectManager.services;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.events.SendMailEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class EmailNotificationService implements NotificationService {

	private final JavaMailSender mailSender;
	private final ITemplateEngine templateEngine;

	@Autowired
	public EmailNotificationService(JavaMailSender mailSender,
									@Qualifier("emailTemplateEngine") ITemplateEngine templateEngine) {
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
	}

	@Async
	@EventListener
	@Override
	public void sendNotification(SendMailEvent event) {

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			MimeMessageHelper message =
					new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			message.setSubject(event.getSubject());
			message.setFrom(event.getFromEmail());
			message.setTo(event.getRecipientEmail());

			message.setText(
					templateEngine.process("text/" + event.getTemplateName(), event.getCtx()),
					templateEngine.process("html/" + event.getTemplateName(), event.getCtx())
			);

			// Send Mail
			mailSender.send(mimeMessage);

		} catch (MessagingException ex) {
			log.error("Exception sending mail message: {}", ex.getMessage());
		}
	}
}
