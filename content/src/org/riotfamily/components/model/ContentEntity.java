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

import org.riotfamily.common.hibernate.ActiveRecord;

/**
 * Base class for entities that act as {@link ContentContainerOwner}s.
 */
@MappedSuperclass
public abstract class ContentEntity extends ActiveRecord
		implements ContentContainerOwner {

	private ContentContainer contentContainer;

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

	@Transient
	public boolean isPublished() {
		return getContentContainer().isPublished();
	}
	
}
