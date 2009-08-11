package org.riotfamily.components.index;

import org.riotfamily.components.model.Content;

public interface ContentIndexer {

	public void contentCreated(Content content) throws Exception;
	
	public void contentDeleted(Content content) throws Exception;
	
	public void contentModified(Content content) throws Exception;
	
}
