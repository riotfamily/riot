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
