package org.riotfamily.core.security.session;

import java.io.Serializable;
import java.util.HashSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.auth.UserLookupAuthenticationService;
import org.springframework.util.ObjectUtils;

/**
 * Class that holds a reference to a RiotUser. An instance of this class is
 * stored in the HttpSession. Additionally each instance is placed in a static
 * set which allows us to access/update all currently logged in users. 
 * <p>
 * The class also implements the HttpSessionBindingListener interface and
 * persists the SessionMetaData as soon as the session expires.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class UserHolder implements Serializable, HttpSessionBindingListener {

	private static final String SESSION_KEY = UserHolder.class.getName();
	
	private static HashSet<UserHolder> users = new HashSet<UserHolder>();
	
	private transient RiotUser user;
	
	private String userId;
	
	private SessionMetaData metaData;
	
	
	private UserHolder(RiotUser user, SessionMetaData metaData) {
		this.user = user;
		this.userId = user.getUserId();
		this.metaData = metaData;
	}
	
	public RiotUser getUser() {
		return this.user;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public SessionMetaData getSessionMetaData() {
		return this.metaData;
	}
	
	/**
	 * Adds itself to the static set of users.
	 * 
	 * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent event) {
		addToStaticUsersSet();
	}
	
	/**
	 * Removes itself from the static set of users and persists the 
	 * SessionMetaData.
	 * 
	 * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		users.remove(this);
		ServletContext sc = event.getSession().getServletContext();
		LoginManager.getInstance(sc).sessionEnded(metaData);
	}
	
	/**
	 * Reloads the transient user object.
	 */
	protected RiotUser reloadUser(UserLookupAuthenticationService lookupService) {
		if (lookupService != null) {
			user = lookupService.getUserById(userId);
			addToStaticUsersSet();
			return user;
		}
		return null;
	}
	
	private void addToStaticUsersSet() {
		users.add(this);
	}
	
	public int hashCode() {
		return userId != null ? userId.hashCode() : 0;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof UserHolder) {
			UserHolder other = (UserHolder) obj;
			return ObjectUtils.nullSafeEquals(userId, other.userId);
		}
		return false;
	}
	
	// --- Static methods -----------------------------------------------------
	
	static void storeInSession(RiotUser user, SessionMetaData metaData, 
			HttpSession session) {
		
		session.setAttribute(SESSION_KEY, new UserHolder(user, metaData));
	}
	
	static UserHolder getInstance(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (UserHolder) session.getAttribute(SESSION_KEY);
	}
	
	/**
	 * Looks up the {@link UserHolder} for the specified userId and replaces 
	 * the user with the given instance.
	 */
	static void updateUser(String userId, RiotUser user) {
		for (UserHolder holder : users) {
			if (holder.getUser() != null 
					&& userId.equals(holder.getUserId())) {
				
				holder.user = user;
			}
		}
	}
		
}
