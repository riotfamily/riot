package org.riotfamily.pages.mail;

import java.util.HashMap;
import java.util.Map;

public class MailForm {

	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String message;
	
	private String subject;
	
	private Map extras = new HashMap();

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map getExtras() {
		return this.extras;
	}

	public void setExtras(Map extras) {
		this.extras = extras;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
