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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.security.policy;

import org.riotfamily.core.security.PermissionDeniedException;
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
