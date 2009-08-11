package org.riotfamily.core.security.policy;

import org.riotfamily.core.security.auth.RiotUser;

public interface AssertionPolicy extends AuthorizationPolicy {

    /**
	 * By contract this method is invoked whenever an action is about to be 
	 * executed. Implementors can use this hook to veto a previously granted
	 * permission.   
	 * 
	 * @param subject The user
	 * @param action The action to be performed
	 * @param object The object on which the action is to be performed
	 * @throws PermissionDeniedException if the permission is not granted
	 */
    public void assertIsGranted(RiotUser user, String action, Object object) 
    		throws PermissionDeniedException;
}
