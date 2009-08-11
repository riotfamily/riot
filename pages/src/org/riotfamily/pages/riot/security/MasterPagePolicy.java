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
package org.riotfamily.pages.riot.security;

import static org.riotfamily.core.security.policy.AuthorizationPolicy.Permission.ABSTAIN;
import static org.riotfamily.core.security.policy.AuthorizationPolicy.Permission.DENIED;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.policy.ReflectionPolicy;
import org.riotfamily.pages.model.Page;

public class MasterPagePolicy extends ReflectionPolicy {
	
	public MasterPagePolicy() {
		setOrder(Integer.MAX_VALUE - 2);
	}
	
	public Permission translatePage(RiotUser user, Page page, CommandContext context) {
		if (context.getParent() == null 
				|| page.getSite().equals(context.getParent())) {
			
			return DENIED;
		}
		return ABSTAIN;
	}
	
	public Permission getPermission(RiotUser user, String action, 
			Page page, CommandContext context) {
		
		if (context.getParent() != null 
				&& !page.getSite().equals(context.getParent())) {
			
			return DENIED;
		}
		return ABSTAIN;
	}
	
}
