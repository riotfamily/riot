package org.riotfamily.media.processing;

import org.riotfamily.media.model.RiotFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 * @deprecated
 */
public interface FileProcessor {

	public void process(RiotFile file) throws FileProcessingException;

}
