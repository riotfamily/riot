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
package org.riotfamily.components.model;

import java.util.ArrayList;

import org.riotfamily.common.collection.DirtyCheckList;
import org.springframework.util.Assert;

/**
 * List of {@link Component}s that is identified by an id.
 */
public class ComponentList extends DirtyCheckList<Component> 
		implements ContentFragment {
	
	private String fragmentId;
	
	private Content owner;

	private String path;
	
	public ComponentList(ContentMap parent, String key) {
		super(new ArrayList<Component>());
		this.owner = parent.getContent();
		this.fragmentId = owner.nextFragmentId();
		Assert.notNull(owner, "owner must not be null");
		Assert.notNull(fragmentId, "fragmentId must not be null");
		this.path = parent.getPath() != null ? parent.getPath() + " " + key : key;
		owner.registerFragment(this);
	}
	
	public ComponentList(Content owner, String fragmentId, String path) {
		super(new ArrayList<Component>());
		Assert.notNull(owner, "owner must not be null");
		Assert.notNull(fragmentId, "fragmentId must not be null");
		this.owner = owner;
		this.fragmentId = fragmentId;
		this.path = path;
		owner.registerFragment(this);
	}

	@Override
	protected void dirty() {
		owner.fragmentModified();
	}
	
	public String getFragmentId() {
		return fragmentId;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getCompositeId() {
		return owner.getCompositeId(this);
	}

	public Content getContent() {
		return owner;
	}

	@Override
	public int hashCode() {
		return fragmentId.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ComponentList) {
			ComponentList other = (ComponentList) o;
			return getCompositeId().equals(other.getCompositeId());
		}
		return false;
	}
	
	public static ComponentList load(String listId) {
		return (ComponentList) Content.loadFragment(listId);
	}
	
}
