package org.riotfamily.riot.workflow.notification;




public interface Notification {	
	
	/**
	 * Returns an id that can be used to mark a notification as read
	 * via a {@link NotificationDao}.
	 */
	public Long getId();
	
	/**
	 * Returns the message text which may contain HTML markup.
	 */
	public String getMessage();
	
	/**
	 * Returns the date when the notification was issued.
	 */
	public String getIssueDate();
	
	public String getCategory();

}
