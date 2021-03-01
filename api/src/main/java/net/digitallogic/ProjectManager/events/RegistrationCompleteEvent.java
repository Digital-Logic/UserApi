package net.digitallogic.ProjectManager.events;

import lombok.Getter;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

	private final UserEntity user;

	public RegistrationCompleteEvent(UserEntity user) {
		super(user);
		this.user = user;
	}
}
