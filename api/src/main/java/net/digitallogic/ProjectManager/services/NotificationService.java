package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.events.SendMailEvent;

public interface NotificationService {
	public void sendNotification(SendMailEvent event);
}
