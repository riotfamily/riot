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
package org.riotfamily.pages.config;

import java.util.Collections;
import java.util.List;

public class VirtualPage implements PageType {

	private Object handler;
	
	private VirtualPage child;
	
	public List<? extends PageType> getChildTypes() {
		return Collections.singletonList(child);
	}

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getSuffixes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void register(SitemapSchema schema) {
		// TODO Auto-generated method stub
		
	}

	
}
