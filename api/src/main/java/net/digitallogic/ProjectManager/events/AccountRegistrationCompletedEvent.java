package net.digitallogic.ProjectManager.events;

import lombok.Getter;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletRequest;

@Getter
public class AccountRegistrationCompletedEvent extends ApplicationEvent {

	private final UserEntity user;
	private final HttpServletRequest request;

	public AccountRegistrationCompletedEvent(UserEntity user, HttpServletRequest request) {
		super(user);
		this.user = user;
		this.request = request;
	}
}
