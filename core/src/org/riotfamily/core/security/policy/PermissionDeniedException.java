package org.riotfamily.core.security.policy;

import org.riotfamily.core.security.auth.RiotUser;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PermissionDeniedException extends RuntimeException {

	private RiotUser user;
	
	private String action;
	
	private Object object;

	private AuthorizationPolicy policy;
	
	private String permissionRequestUrl;

	public PermissionDeniedException(RiotUser user, String action, Object object, 
			AuthorizationPolicy policy) {
		
		this(user, action, object, policy, null);
	}
	
	public PermissionDeniedException(RiotUser user, String action, Object object, 
			AuthorizationPolicy policy, String permissionRequestUrl) {
		
		this.user = user;
		this.action = action;
		this.object = object;
		this.policy = policy;
		this.permissionRequestUrl = permissionRequestUrl;
	}

	public RiotUser getUser() {
		return this.user;
	}

	public String getAction() {
		return this.action;
	}

	public Object getObject() {
		return this.object;
	}

	public AuthorizationPolicy getPolicy() {
		return this.policy;
	}
	
	public String getPermissionRequestUrl() {
		return permissionRequestUrl;
	}
}
