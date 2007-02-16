/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
