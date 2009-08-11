package org.riotfamily.core.security.policy;

import org.riotfamily.core.security.auth.RiotUser;

/**
 * Default RiotPolicy that always returns <code>true</code>.
 */
public class GrantAllPolicy implements AuthorizationPolicy {
    
	private int order = Integer.MAX_VALUE;
	
    public int getOrder() {
		return this.order;
	}
	
    public void setOrder(int order) {
		this.order = order;
	}

	public Permission getPermission(RiotUser user, String action, Object object) {
        return Permission.GRANTED;
    }
	
	public void assertIsGranted(RiotUser user, String action, Object object)
			throws PermissionDeniedException {
		
	}

}
