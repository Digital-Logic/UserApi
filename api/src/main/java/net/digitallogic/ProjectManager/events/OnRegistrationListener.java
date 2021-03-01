package net.digitallogic.ProjectManager.events;

import net.digitallogic.ProjectManager.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OnRegistrationListener {

	private final AuthService authService;

	@Autowired
	public OnRegistrationListener(AuthService authService) {
		this.authService = authService;
	}

	@Async
	@TransactionalEventListener(value = RegistrationCompleteEvent.class, phase= TransactionPhase.AFTER_COMPLETION)
	public void OnRegistrationComplete(RegistrationCompleteEvent event) {
		// Create Activation token
		authService.createAccountActivationToken(event.getUser());
	}
}
