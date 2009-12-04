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

import junit.framework.Assert;

import org.junit.Test;
import org.riotfamily.core.screen.DefaultScreenContext;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.auth.User;
import org.riotfamily.core.security.policy.AuthorizationPolicy.Permission;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

public class ReflectionPolicyTest {

	private AuthorizationPolicy policy = new TestPolicy();
	
	private RiotUser user = new User();
	
	private ScreenContext context = new DefaultScreenContext(null, null, null, null, false);
	
	@Test
	public void test() {
		Assert.assertEquals(Permission.GRANTED, policy.getPermission(user, "edit", new ContentPage(), context));
		Assert.assertEquals(Permission.DENIED, policy.getPermission(user, "edit", new Site(), context));
		Assert.assertEquals(Permission.DENIED, policy.getPermission(user, "delete", new Site(), context));
		Assert.assertEquals(Permission.GRANTED, policy.getPermission(user, "delete", new ContentPage(), context));
	}
	
	public class TestPolicy extends ReflectionPolicy {
		
		public Permission edit(User user, Page page, ScreenContext context) {
			return Permission.GRANTED;
		}
		
		public Permission edit(User user, ScreenContext context) {
			return Permission.DENIED;
		}
		
		public Permission getPermission(User user, Site site, ScreenContext context) {
			Assert.fail("Must not be invoked because the second argument is not a String");
			return null;
		}
		
		public Permission getPermission(User user, String action, Site site, ScreenContext context) {
			return Permission.DENIED;
		}
		
		public Permission getPermission(User user, String action, ScreenContext context) {
			return Permission.GRANTED;
		}
		
	}
}
