package org.riotfamily.riot.workflow.notification.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.riotfamily.riot.workflow.notification.Notification;
import org.riotfamily.riot.workflow.notification.NotificationDao;

public class DefaultNotificationDao implements NotificationDao {
	
	private List notifications = new ArrayList();
	
	private Map notificationsReadByUser = new HashMap();
	
	
	public DefaultNotificationDao() {
	}
	
	public List getNotifications(String userId) {
		Set readNotificationIds = (Set) notificationsReadByUser.get(userId);
		if (readNotificationIds == null) {
			return notifications;
		}
		else {
			return getUnreadNotifications(readNotificationIds);
		}		
	}
	
	private List getUnreadNotifications(Set readNotificationIds) {
		List unreadNotifications = new ArrayList();
		for (int i = 0; i < notifications.size(); i++) {
			Notification notification = (Notification) notifications.get(i);
			if (!readNotificationIds.contains(notification.getId())) {
				unreadNotifications.add(notification);
			}
		}
		return unreadNotifications;
	}

	public void markAsRead(String userId, Long notificationId) {
		Set readNotifications = (Set) notificationsReadByUser.get(userId);
		if (readNotifications == null) {
			readNotifications = new HashSet();
			notificationsReadByUser.put(userId, readNotifications);
		}
		readNotifications.add(notificationId);		
	}
		
	public void saveNotification(Notification notification) {
		notifications.add(notification);
	}

}
