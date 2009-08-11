package org.riotfamily.media.processing;

import java.util.List;

import org.riotfamily.media.model.RiotFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 * @deprecated
 */
public class BatchProcessor implements FileProcessor {

	private List<FileProcessor> processors;
	
	public void setProcessors(List<FileProcessor> processors) {
		this.processors = processors;
	}

	public void process(RiotFile data) {
		if (processors != null) {
			for (FileProcessor processor : processors) {
				processor.process(data);
			}
		}
	}
}
