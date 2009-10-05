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

import java.util.Collection;

import org.hibernate.Session;
import org.riotfamily.common.hibernate.TypedEntityListener;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.components.model.Content;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ContentListener extends TypedEntityListener<Content> 
		implements ApplicationContextAware {

	private Collection<ContentIndexer> indexers;
	
	public void setApplicationContext(ApplicationContext ctx) {
		indexers = SpringUtils.listBeansOfType(ctx, ContentIndexer.class);
	}
	
	@Override
	protected void entitySaved(Content content, Session session)
		throws Exception {
		
		if (indexers != null) {
			for (ContentIndexer indexer : indexers) {
				indexer.contentCreated(content);
			}
		}
	}
	
	@Override
	protected void entityDeleted(Content content, Session session)
			throws Exception {
		
		if (indexers != null) {
			for (ContentIndexer indexer : indexers) {
				indexer.contentDeleted(content);
			}
		}
	}
	
	@Override
	protected void entityUpdated(Content content, Content oldState, 
			Session session) throws Exception {
		
		if (indexers != null) {
			for (ContentIndexer indexer : indexers) {
				indexer.contentModified(content);
			}
		}
	}
}
