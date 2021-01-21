package net.digitallogic.ProjectManager.web.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class MessageConverter {
	protected final MessageCode messageCode;
	protected final List<Object> args;

	public MessageConverter(MessageCode messageCode, List<Object> args) {
		this.messageCode = messageCode;
		this.args = args;
	}

	public MessageConverter(MessageCode messageCode, Object... args) {
		this(messageCode, List.of(args));
	}
	public MessageConverter(MessageCode messageCode) {
		this(messageCode, List.of());
	}

	public String getMessageCode() {
		return this.messageCode.property;
	}
}
