/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.security.policy;

import org.riotfamily.core.security.auth.RiotUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A logging policy for debugging purposes.
 * 
 * @since 6.5
 * @author Alf Werder [alf dot werder at artundweise dot de]
 */
public class LoggingPolicy implements AuthorizationPolicy {
	
    private Logger log = LoggerFactory.getLogger(LoggingPolicy.class);
    
	private int order = org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
	
    public int getOrder() {
		return this.order;
	}
	
    public void setOrder(int order) {
		this.order = order;
	}

	public Permission getPermission(RiotUser user, String action, Object object, Object context) {
		if (log.isDebugEnabled()) {
        	log.debug(getMessage(action, object, context));
        }
        return Permission.ABSTAIN;
    }
		
	private String getMessage(String action, Object object, Object context) {
		return String.format("action: %s, object: %s, context: %s", action, object, context);
	}

}
