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

import java.util.HashMap;

import org.riotfamily.common.collection.DirtyCheckMap;
import org.springframework.util.Assert;

public class ContentMapImpl extends DirtyCheckMap<String, Object> 
		implements ContentMap {

	private String fragmentId;
	
	private Content owner;

	public ContentMapImpl(Content owner) {
		this(owner, owner.nextFragmentId());
	}
	
	public ContentMapImpl(Content owner, String fragmentId) {
		super(new HashMap<String, Object>());
		Assert.notNull(owner, "owner must not be null");
		Assert.notNull(fragmentId, "fragmentId must not be null");
		this.owner = owner;
		this.fragmentId = fragmentId;
		owner.registerFragment(this);
	}

	/**
	 * Notifies the owner that the content has been modified.
	 */
	@Override
	protected void dirty() {
		owner.fragmentModified();
	}
	
	public String getFragmentId() {
		return fragmentId;
	}

	public String getCompositeId() {
		return owner.getCompositeId(this);
	}
	
	public String getPath() {
		return null;
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
		if (o instanceof ContentMapImpl) {
			ContentMapImpl other = (ContentMapImpl) o;
			return getCompositeId().equals(other.getCompositeId());
		}
		return false;
	}

}
