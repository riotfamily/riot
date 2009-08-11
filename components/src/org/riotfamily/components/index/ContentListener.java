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
