package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.events.SendMailEvent;

public interface NotificationService {
	void sendNotification(SendMailEvent event);
}
