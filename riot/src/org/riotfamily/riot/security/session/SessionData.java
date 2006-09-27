package org.riotfamily.riot.security.session;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class SessionData implements Serializable, HttpSessionBindingListener {

	private transient SessionDataStore store;
	
	private static final String SESSION_KEY = SessionData.class.getName();
	
	private String principal;
	
	private String username;
	
	private Date loginDate;
	
	private Date lastLoginDate;
	
	private String loginIP;
	
	private String lastLoginIP;

	public static SessionData get(HttpServletRequest request) {
		return (SessionData) request.getSession().getAttribute(SESSION_KEY);
	}
	
	public void newSession(HttpServletRequest request, SessionDataStore store) {
		loginDate = new Date();
		loginIP = request.getRemoteAddr();
		this.store = store;
		request.getSession().setAttribute(SESSION_KEY, this);
	}
	
	protected void sessionEnded() {
		lastLoginDate = loginDate;
		lastLoginIP = loginIP;
	}
	
	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getLastLoginIP() {
		return lastLoginIP;
	}

	public void setLastLoginIP(String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}
	
	public void valueBound(HttpSessionBindingEvent event) {
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		sessionEnded();
		if (store != null) {
			store.storeSessionData(this);
		}
	}
	
}
