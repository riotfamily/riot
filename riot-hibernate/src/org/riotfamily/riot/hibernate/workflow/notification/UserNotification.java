package org.riotfamily.riot.hibernate.workflow.notification;

import org.riotfamily.riot.hibernate.security.User;
import org.riotfamily.riot.workflow.notification.support.DefaultNotification;

public class UserNotification {

	private Long id;
	
	private User user; 
	
	private DefaultNotification notification;
	
	private boolean read;	

	public UserNotification() {
	}
	
	public UserNotification(User user, DefaultNotification notification) {		
		this.user = user;
		this.notification = notification;
	}

	public DefaultNotification getNotification() {
		return notification;
	}

	public void setNotification(DefaultNotification notification) {
		this.notification = notification;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
}
