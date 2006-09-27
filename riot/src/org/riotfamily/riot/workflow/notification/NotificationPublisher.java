package org.riotfamily.riot.workflow.notification;

import java.util.Locale;

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
		
		String message = messageSource.getMessage(
				messageKey, args, Locale.getDefault());
		
		publishNotification(message, category);
	}
	
	public void publishNotification(String message, String category) {
		if (category == null) {
			category = DEFAULT_CATEGORY;
		}
		Notification notification = notificationFactory.createNotification(message, category);
		notificationDao.saveNotification(notification);		
	}	
	
}
