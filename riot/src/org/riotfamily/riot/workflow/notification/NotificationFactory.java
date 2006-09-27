package org.riotfamily.riot.workflow.notification;

public interface NotificationFactory {

	public Notification createNotification(String message, String category);
	
}
