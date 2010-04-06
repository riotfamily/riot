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

import org.junit.Assert;
import org.junit.Test;
import org.riotfamily.core.screen.DefaultScreenContext;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.auth.User;

public class ReflectionPolicyTest {

	private TestPolicy policy = new TestPolicy();
	
	private RiotUser user = new User();
	
	private ScreenContext context = new DefaultScreenContext(null, null, null, null, false);
	
	@Test
	public void test() {
		policy.getPermission(user, "edit", new Page(), context);
		Assert.assertEquals(1, policy.getInvokedMethod());
		
		policy.getPermission(user, "edit", null, context);
		Assert.assertEquals(2, policy.getInvokedMethod());
		
		policy.getPermission(user, "edit", new Site(), context);
		Assert.assertEquals(2, policy.getInvokedMethod());
		
		policy.getPermission(user, "delete", new Site(), context);
		Assert.assertEquals(3, policy.getInvokedMethod());
				
		policy.getPermission(user, "delete", new Page(), context);
		Assert.assertEquals(4, policy.getInvokedMethod());
		
		policy.getPermission(user, "edit", new Page(), null);
		Assert.assertEquals(0, policy.getInvokedMethod());
	}
	
	public static class TestPolicy extends ReflectionPolicy {
		
		private int invokedMethod;
		
		public int getInvokedMethod() {
			int i = invokedMethod;
			invokedMethod = 0;
			return i;
		}
		
		public Permission edit(User user, Page page, ScreenContext context) {
			invokedMethod = 1;
			return Permission.GRANTED;
		}
		
		public Permission edit(User user, ScreenContext context) {
			invokedMethod = 2;
			return Permission.DENIED;
		}
		
		public Permission getPermission(User user, String action, Site site, ScreenContext context) {
			invokedMethod = 3;
			return Permission.DENIED;
		}
		
		public Permission getPermission(User user, String action, ScreenContext context) {
			invokedMethod = 4;
			return Permission.GRANTED;
		}

		public Permission getPermission(User user, Site site, ScreenContext context) {
			Assert.fail("Must not be invoked because the second argument is not a String");
			return null;
		}
		
		public Permission getPermission(User user, String action) {
			Assert.fail("Must not be invoked because at least one argument is missing");
			return null;
		}
		
	}
	
	public static class Site {
	}
	
	public static class Page {
	}
}
