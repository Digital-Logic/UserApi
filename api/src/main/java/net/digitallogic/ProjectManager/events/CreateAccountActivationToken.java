package net.digitallogic.ProjectManager.events;

import lombok.Getter;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Getter
public class CreateAccountActivationToken extends ApplicationEvent {

	private final UserEntity user;
	private final ServletUriComponentsBuilder uriComponentsBuilder;

	public CreateAccountActivationToken(UserEntity user) {
		super(user);
		this.user = user;

		uriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
	}
}
