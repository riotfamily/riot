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

import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Site;

public class SystemPageType extends ContentPageType implements VirtualPageParent {

	private String pathComponent;
	
	private List<SystemPageType> childPages;
	
	private VirtualPageType virtualChildType;
	
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

	public List<SystemPageType> getChildPages() {
		return childPages;
	}

	public void setChildPages(List<SystemPageType> childPages) {
		this.childPages = childPages;
	}

	/* (non-Javadoc)
	 * @see org.riotfamily.pages.config.VirtualPageParent#getVirtualChildType()
	 */
	public VirtualPageType getVirtualChildType() {
		return virtualChildType;
	}

	public void setVirtualPage(VirtualPageType virtualChildType) {
		this.virtualChildType = virtualChildType;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	private void sync(ContentPage parent) {
		ContentPage page = ContentPage.loadByTypeAndSite(getName(), parent.getSite());
		if (page == null) {
			page = createPage(parent.getSite(), parent);
		}
		update(page);
	}
	
	protected ContentPage createPage(Site site, ContentPage parent) {
		ContentPage page = new ContentPage(getPathComponent(), site);
		if (properties != null) {
			page.getContentContainer().getPreviewVersion().putAll(properties);
		}
		if (parent != null) {
			parent.addPage(page);
		}
		page.save();
		return page;
	}

	protected void update(ContentPage page) {
		page.setPageType(getName());
		if (childPages != null) {
			for (SystemPageType child : childPages) {
				child.sync(page);
			}
		}
	}

	@Override
	public void register(SitemapSchema schema, PageType parent) {
		super.register(schema, parent);
		if (childPages != null) {
			for (SystemPageType child : childPages) {
				child.register(schema, this);
			}
		}
		if (virtualChildType != null) {
			virtualChildType.register(schema, this);
		}
	}
}
