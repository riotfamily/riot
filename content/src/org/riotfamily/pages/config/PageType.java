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

import java.util.List;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;


public interface PageType {

	public String getName();

	public String getForm();
	
	public Object getHandler();
	
	public PageType getParent();
	
	public List<? extends PageType> getChildTypes();

	public Page getPage(Site site, Object... args);
	
	void register(SitemapSchema schema, PageType parent);
	
}
