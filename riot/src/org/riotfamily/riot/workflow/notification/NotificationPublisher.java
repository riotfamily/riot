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
package org.riotfamily.riot.workflow.notification;

import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class NotificationPublisher implements MessageSourceAware {

	public static final String DEFAULT_CATEGORY = "system";
	
	private NotificationFactory notificationFactory;
	
	private NotificationDao notificationDao;
	
	private MessageSource messageSource;
	
	public NotificationPublisher(NotificationFactory notificationFactory, 
			NotificationDao notificationDao) {
		
		this.notificationFactory = notificationFactory;
		this.notificationDao = notificationDao;
	}
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void publishNotification(String message) {
		publishNotification(message, null);
	}
	
	public void publishNotification(String messageKey, Object[] args, 
			String category) {
		
		String message = messageSource.getMessage(messageKey, 
				FormatUtils.htmlEscapeArgs(args), Locale.getDefault());
		
		publishNotification(message, category);
	}
	
	public void publishNotification(String message, String category) {
		if (category == null) {
			category = DEFAULT_CATEGORY;
		}
		Notification notification = notificationFactory.createNotification(
				message, category);
		
		notificationDao.saveNotification(notification);		
	}	
	
}
