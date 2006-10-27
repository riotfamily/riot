/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
