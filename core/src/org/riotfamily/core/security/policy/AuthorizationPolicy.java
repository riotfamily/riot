package org.riotfamily.core.security.policy;

import org.riotfamily.core.security.auth.RiotUser;
import org.springframework.core.Ordered;


/**
 * Interface to check if a user has the permission to perform a certain action.
 */
public interface AuthorizationPolicy extends Ordered {

	public enum Permission {
		ABSTAIN, DENIED, GRANTED
	}
	
	/**
	 * Returns the permission for the given user, action and object.
	 * 
	 * @param subject The user
	 * @param action The action to be performed
	 * @param object The object on which the action is to be performed
	 */
    public Permission getPermission(RiotUser user, String action, Object object);

}
