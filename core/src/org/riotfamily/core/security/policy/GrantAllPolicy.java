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

	public Permission getPermission(RiotUser user, String action, Object object, Object context) {
        return Permission.GRANTED;
    }
	
	public void assertIsGranted(RiotUser user, String action, Object object, Object context)
			throws PermissionDeniedException {
		
	}

}
