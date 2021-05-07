package net.digitallogic.UserApi.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SendMailEvent extends ApplicationEvent {
	private final String templateName;
	private final String recipientEmail;
	private final String fromEmail;
	private final String subject;

	private final Context ctx;

	public SendMailEvent(Object source, String templateName, String recipientEmail, String fromEmail, String subject, Context ctx) {
		super(source);

		this.templateName = templateName;
		this.recipientEmail = recipientEmail;
		this.fromEmail = fromEmail;
		this.subject = subject;
		this.ctx = ctx;
	}

	public static SendMailEventBuilder builder() {return new SendMailEventBuilder();}

	public static class SendMailEventBuilder {
		private Object source;
		private String templateName;
		private String recipientEmail;
		private String fromEmail;
		private String subject;
		private Context ctx = new Context();
		private final Map<String, Object> variableMap = new HashMap<>();

		SendMailEventBuilder() {}

		public SendMailEventBuilder source(Object source) {
			this.source = source;
			return this;
		}

		public SendMailEventBuilder templateName(String templateName) {
			this.templateName = templateName;
			return this;
		}

		public SendMailEventBuilder recipientEmail(String recipientEmail) {
			this.recipientEmail = recipientEmail;
			return this;
		}

		public SendMailEventBuilder fromEmail(String fromEmail) {
			this.fromEmail = fromEmail;
			return this;
		}

		public SendMailEventBuilder subject(String subject) {
			this.subject = subject;
			return this;
		}

		public SendMailEventBuilder ctx(Context ctx) {
			this.ctx = ctx;
			return this;
		}

		public SendMailEventBuilder addVariable(String property, Object value) {
			this.variableMap.put(property, value);
			return this;
		}

		public SendMailEvent build() {
			Assert.notNull(source, "Source must not be null.");
			Assert.notNull(templateName, "TemplateName must not be null.");
			Assert.notNull(recipientEmail, "RecipientEmail must not be null.");
			Assert.notNull(fromEmail, "FromEmail must not be null.");
			Assert.notNull(subject, "Subject must not be null.");

			variableMap.forEach((p, v) -> ctx.setVariable(p, v));

			return new SendMailEvent(source, templateName, recipientEmail, fromEmail, subject, ctx);
		}

		public String toString() {return "SendMailEvent.SendMailEventBuilder(templateName=" + this.templateName + ", recipientEmail=" + this.recipientEmail + ", fromEmail=" + this.fromEmail + ", subject=" + this.subject + ", ctx=" + this.ctx + ")";}
	}
}
