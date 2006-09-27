package org.riotfamily.riot.security.policy;

import org.riotfamily.riot.editor.EditorDefinition;
import org.springframework.core.Ordered;


/**
 * Interface to check if a user has the permission to perform a certain action.
 */
public interface AuthorizationPolicy extends Ordered {

	public int ACCESS_ABSTAIN = 0;
	
	public int ACCESS_DENIED = 1;
	
	public int ACCESS_GRANTED = 2;
	/**
	 * Checks whether the given user is allowed to perform the specified action.
	 * @param subject The subject (userId)
	 * @param action The action to be performed
	 * @param object The object on which the action is to be performed
	 * @param editor The editor being invovled (may be null)
	 */
    public int checkPermission(String subject, String action, Object object, 
    		EditorDefinition editor);
        

}
