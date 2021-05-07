package net.digitallogic.UserApi.services;

import net.digitallogic.UserApi.events.SendMailEvent;

public interface NotificationService {
	void sendNotification(SendMailEvent event);
}
