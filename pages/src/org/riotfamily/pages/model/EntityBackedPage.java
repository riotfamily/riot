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

import org.riotfamily.common.hibernate.ActiveRecordUtils;
import org.riotfamily.components.model.ContentEntity;
import org.riotfamily.pages.config.VirtualPageType;

/**
 * VirtualPage that is backed by a {@link ContentEntity}.
 */
public class EntityBackedPage extends VirtualPage {

	private ContentEntity entity;
	
	public EntityBackedPage(VirtualPageType pageType, Page parent,
			ContentEntity entity, String title) {
		
		super(pageType, parent, entity, ActiveRecordUtils.getId(entity).toString(), title);
		this.entity = entity;
	}
	
	@Override
	public void tag() {
		entity.tag();
	}

}
