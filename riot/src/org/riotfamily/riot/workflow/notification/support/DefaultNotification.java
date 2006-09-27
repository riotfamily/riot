package org.riotfamily.riot.workflow.notification.support;

import java.util.Date;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.workflow.notification.Notification;

public class DefaultNotification implements Notification {	

	private Long id;
	
	private String message;
	
	private String category;
	
	private String issueDate;
	
	public DefaultNotification() {
		issueDate = FormatUtils.formatIsoDate(new Date());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIssueDate() {		
		return issueDate;
	}
	
	public void setIssueDate(String date) {
		this.issueDate = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
