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
import java.util.Map;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

public class SystemPage extends PageType {

	private String pathComponent;
	
	private List<SystemPage> childPages;
	
	private Map<String, Object> properties;

	public String getPathComponent() {
		if (pathComponent == null) {
			return getName();
		}
		return pathComponent;
	}

	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}

	public List<SystemPage> getChildPages() {
		return childPages;
	}

	public void setChildPages(List<SystemPage> childPages) {
		this.childPages = childPages;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	private void sync(Page parent) {
		Page page = Page.loadByTypeAndSite(getName(), parent.getSite());
		if (page == null) {
			page = createPage(parent.getSite(), parent);
		}
		update(page);
	}
	
	protected Page createPage(Site site, Page parent) {
		Page page = new Page(getPathComponent(), site);
		if (properties != null) {
			page.getContentContainer().getPreviewVersion().putAll(properties);
		}
		if (parent != null) {
			parent.addPage(page);
		}
		page.save();
		return page;
	}

	protected void update(Page page) {
		page.setPageType(getName());
		if (childPages != null) {
			for (SystemPage child : childPages) {
				child.sync(page);
			}
		}
	}

	@Override
	void register(SitemapSchema schema) {
		super.register(schema);
		if (childPages != null) {
			for (SystemPage child : childPages) {
				child.register(schema);
			}
		}
	}
}
