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
 * Neteye GmbH
 * artundweise GmbH
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *   Alf Werder [alf dot werder at artundweise dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.security.policy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.riot.security.auth.RiotUser;

/**
 * A logging policy for debugging purposes.
 * 
 * @since 6.5
 * @author Alf Werder [alf dot werder at artundweise dot de]
 */
public class LoggingPolicy implements AuthorizationPolicy {
	
    private Log log = LogFactory.getLog(LoggingPolicy.class);
    
	private int order = Integer.MIN_VALUE;
	
    public int getOrder() {
		return this.order;
	}
	
    public void setOrder(int order) {
		this.order = order;
	}

	public int checkPermission(RiotUser user, String action, Object object) {
		if (log.isDebugEnabled()) {
			StringBuffer message = new StringBuffer();
			message.append("user: [").append(user.getUserId()).append("], ");
			message.append("action: [").append(action).append("], ");
			message.append("object: ");
			if (object != null) {
				if (object.getClass().isArray()) {
					Object[] objects = (Object[]) object;
					for (Object o : objects) {
						message.append(o.getClass().getName());
						message.append(',');
					}
				}
				else {
					message.append(object.getClass().getName());
				}
			}
			message.append("[").append(object).append("]");
			
        	log.debug(message.toString());
        }
        return ACCESS_ABSTAIN;
    }

}
