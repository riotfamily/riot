package org.riotfamily.riot.workflow.notification;

import java.util.List;


public interface NotificationDao {
	
	public List getNotifications(String userId);
	
	public void markAsRead(String userId, Long notificationId);
	
	public void saveNotification(Notification notification);

}
