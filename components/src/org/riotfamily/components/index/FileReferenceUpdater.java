package org.riotfamily.components.index;

import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.RiotFileReference;
import org.riotfamily.media.model.RiotFile;

public class FileReferenceUpdater extends AbstractContentIndexer {

	@Override
	protected void createIndex(Content content) throws Exception {
		for (Object obj : content.getReferences()) {
			if (obj instanceof RiotFile) {
				new RiotFileReference(content, (RiotFile) obj).save();
			}
		}
	}

	@Override
	protected void deleteIndex(Content content) {
		RiotFileReference.deleteByContent(content);
	}

}
