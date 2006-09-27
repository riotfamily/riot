package org.riotfamily.riot.workflow.notification.support;

import org.riotfamily.riot.workflow.notification.NotificationDao;
import org.riotfamily.riot.workflow.notification.NotificationFactory;
import org.riotfamily.riot.workflow.notification.NotificationPublisher;
import org.riotfamily.riot.workflow.notification.NotificationPublisherAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;

public class NotificationPublisherAwareProcessor implements BeanPostProcessor,
		MessageSourceAware, InitializingBean {
	
	private NotificationPublisher notificationPublisher;

	private NotificationFactory notificationFactory;
	
	private NotificationDao notificationDao;
		
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public void setNotificationFactory(NotificationFactory notificationFactory) {
		this.notificationFactory = notificationFactory;
	}

	public void setNotificationPublisher(NotificationPublisher notificationPublisher) {
		this.notificationPublisher = notificationPublisher;
	}

	public void afterPropertiesSet() throws Exception {
		if (notificationPublisher == null) {
			Assert.notNull(notificationFactory, "A NotificationFactory is required.");
			Assert.notNull(notificationDao, "A NotificationDao is required.");
			notificationPublisher = new NotificationPublisher(
					notificationFactory, notificationDao);
			
			notificationPublisher.setMessageSource(messageSource);
		}
	}
	
	public Object postProcessBeforeInitialization(Object bean, String beanName) {		
		if (bean instanceof NotificationPublisherAware) {
			NotificationPublisherAware npa = (NotificationPublisherAware) bean;
			npa.setNotificationPublisher(notificationPublisher);
		}
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) {		
		return bean;
	}
}
