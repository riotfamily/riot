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
package org.riotfamily.core.screen.list.command;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.springframework.context.support.MessageSourceAccessor;


public interface CommandContext {

	public HttpServletRequest getRequest();

	public MessageSourceAccessor getMessageSourceAccessor();
	
	public String getResourcePath();
	
	public String getListKey();
	
	public ListParams getParams();
	
	public int getItemsTotal();
	
	public String getCommandId();
		
	public Object getParent();
	
	public ListScreen getScreen();
	
	public ScreenContext getScreenContext();
	
}
