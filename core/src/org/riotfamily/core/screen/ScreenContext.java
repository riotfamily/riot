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
package org.riotfamily.core.screen;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.core.dao.RiotDao;

public interface ScreenContext {

	public class Binding {
		
		private static final String REQUEST_ATTR = "context";
		
		public static void expose(ScreenContext context) {
			context.getRequest().setAttribute(REQUEST_ATTR, context);
		}
		
		public static ScreenContext get(HttpServletRequest request) {
			return (ScreenContext) request.getAttribute(REQUEST_ATTR);
		}
		
	}
	
	public String getObjectId();

	public String getParentId();

	public boolean isNestedTreeItem();
	
	public RiotScreen getScreen();
	
	public RiotDao getDao();
	
	public HttpServletRequest getRequest();

	public ScreenContext createParentContext();
	
	public ScreenContext createNewItemContext(Object parentTreeItem);
	
	public ScreenContext createItemContext(Object item);
	
	public ScreenContext createChildContext(RiotScreen screen);
	
	public Object getObject();
	
	public Object getParent();
	
	public ScreenLink getLink();
	
}
