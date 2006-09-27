package org.riotfamily.riot.workflow.notification.support;

import org.riotfamily.riot.workflow.notification.Notification;
import org.riotfamily.riot.workflow.notification.NotificationFactory;

public class DefaultNotificationFactory implements NotificationFactory {

	public Notification createNotification(String message, String category) {
		
		DefaultNotification notification = new DefaultNotification();
		notification.setCategory(category);
		notification.setMessage(message);
		
		return notification;
	}

}
