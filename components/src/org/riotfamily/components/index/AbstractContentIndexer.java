package org.riotfamily.components.index;

import org.riotfamily.components.model.Content;

public abstract class AbstractContentIndexer implements ContentIndexer {

	public void contentCreated(Content content) throws Exception {
		createIndex(content);
	}

	public void contentDeleted(Content content) throws Exception {
		deleteIndex(content);
	}

	public void contentModified(Content content) throws Exception {
		deleteIndex(content);
		createIndex(content);
	}

	protected abstract void createIndex(Content content) throws Exception;

	protected abstract void deleteIndex(Content content) throws Exception;

}
