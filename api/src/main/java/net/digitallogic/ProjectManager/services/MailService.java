package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.events.SendMailEvent;

public interface MailService {
	public void sendEmail(SendMailEvent event);
}
