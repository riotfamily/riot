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
package org.riotfamily.common.web.mvc.servlet;


/**
 * Bean that is looked up by the {@link ReloadableDispatcherServlet} to 
 * determine whether reload checks should be enabled. If no instance of this 
 * class is found, the DispatcherServlet will use the the value obtained from
 * the <code>reloadable</code> init-parameter.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ReloadableDispatcherServletConfig {

	private boolean reloadable;

	public boolean isReloadable() {
		return this.reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}
	
}
