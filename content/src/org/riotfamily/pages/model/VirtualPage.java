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

import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentContainerOwner;
import org.riotfamily.pages.config.VirtualPageType;

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

	public String getPageType() {
		return pageType.getName();
	}

	public Page getParent() {
		return parent;
	}

	public String getPath() {
		return parent.getPath() + "/" + pathComponent;
	}

	public String getPathComponent() {
		return pathComponent;
	}

	public String getTitle() {
		return title;
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

	public boolean isPublished() {
		return object.isPublished();
	}

	public void setPublished(boolean published) {
		object.setPublished(published);
	}

	public Collection<? extends Page> getChildren() {
		return pageType.listChildren(this);
	}

}
