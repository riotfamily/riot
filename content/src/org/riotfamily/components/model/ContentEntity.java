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

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.website.cache.CacheTagUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Base class for entities that act as {@link ContentContainerOwner}s.
 */
@MappedSuperclass
public abstract class ContentEntity extends ActiveRecord
		implements ContentContainerOwner {

	private ContentContainer contentContainer;

	private boolean published;
	
	private CacheService cacheService;
	
	@Required	
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	@Transient
	protected CacheService getCacheService() {
		return cacheService;
	}
	
	@ManyToOne(cascade=CascadeType.ALL)
	public ContentContainer getContentContainer() {
		if (contentContainer == null) {
			contentContainer = new ContentContainer(this);
		}
		return contentContainer;
	}
	
	public void setContentContainer(ContentContainer contentContainer) {
		this.contentContainer = contentContainer;
	}

	public boolean isPublished() {
		return published;
	}

	@AccessType("field")
	public void setPublished(boolean published) {
		this.published = published;
		invalidateCacheItems();
	}

	protected void invalidateCacheItems() {
		CacheTagUtils.invalidate(cacheService, this);
	}

	public void tag() {
		CacheTagUtils.tag(this);
	}
	
}
