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
package org.riotfamily.pages.model;

import java.util.Collection;
import java.util.Date;

import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentContainerOwner;
import org.riotfamily.pages.config.PageType;
import org.riotfamily.pages.config.VirtualPageType;
import org.springframework.util.ObjectUtils;

/**
 * Page that is backed by a {@link ContentContainerOwner}.
 *
 */
public class VirtualPage implements Page {

	private VirtualPageType pageType;

	private Page parent;
	
	private ContentContainerOwner object;

	private String pathComponent;
	
	private String title;
	
	public VirtualPage(VirtualPageType pageType, Page parent,
			ContentContainerOwner object, String pathComponent, String title) {

		this.pageType = pageType;
		this.parent = parent;
		this.object = object;
		this.pathComponent = pathComponent;
		this.title = title;
	}

	public PageType getPageType() {
		return pageType;
	}

	public Page getParent() {
		return parent;
	}

	public String getPath() {
		String path = parent.getPath();
		if (!path.endsWith("/")) {
			path += "/";
		}
		return path + pathComponent;
	}

	public String getPathComponent() {
		return pathComponent;
	}

	public String getTitle() {
		return title;
	}
	
	public String getUrl() {
		return getPath() + getSite().getDefaultSuffix(this); 
	}
	
	public Site getSite() {
		return parent.getSite();
	}
	
	public Object getObject() {
		return object;
	}
	
	public ContentContainer getContentContainer() {
		return object.getContentContainer();
	}

	public Collection<? extends Page> getChildren() {
		return pageType.listChildren(this);
	}
	
	public Date getLastPublished() {
		return pageType.getResolver().getLastPublished(pageType, parent, object);
	}
	
	@Override
	public int hashCode() {
		return pathComponent != null ? pathComponent.hashCode()  : 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof VirtualPage) {
			VirtualPage other = (VirtualPage) obj;
			return ObjectUtils.nullSafeEquals(pathComponent, other.pathComponent)
				&& ObjectUtils.nullSafeEquals(parent, other.parent);
		}
		return false;
	}

}
