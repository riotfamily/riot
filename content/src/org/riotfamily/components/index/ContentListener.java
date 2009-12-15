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
package org.riotfamily.components.index;

import java.util.List;

import org.hibernate.Session;
import org.riotfamily.common.hibernate.TypedEntityListener;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.components.model.Content;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ContentListener extends TypedEntityListener<Content> implements ApplicationContextAware {

	private List<ContentIndexer> indexers;
	
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	private List<ContentIndexer> getIndexers() {
		if (indexers == null) {
			indexers = Generics.newArrayList();
			for (ContentIndexer indexer : SpringUtils.listBeansOfType(applicationContext, ContentIndexer.class)) {
				if (indexer != this) {
					indexers.add(indexer);
				}
			}
		}
		return indexers;
	}

	@Override
	protected void entitySaved(Content content, Session session) {
		for (ContentIndexer indexer : getIndexers()) {
			indexer.contentCreated(content);
		}
	}
	
	@Override
	protected void entityDeleted(Content content, Session session) {
		for (ContentIndexer indexer : getIndexers()) {
			indexer.contentDeleted(content);
		}
	}
	
	@Override
	protected void entityUpdated(Content content, Content oldState, Session session) {
		for (ContentIndexer indexer : getIndexers()) {
			indexer.contentModified(content);
		}
	}
}
