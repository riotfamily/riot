package org.riotfamily.media.setup;

import java.io.IOException;

import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.processing.FileProcessor;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 7.1
 */
public abstract class AbstractRiotFileFactoryBean extends AbstractFactoryBean {

	private Resource resource;	
	
	private FileProcessor processor;
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void setProcessor(FileProcessor processor) {
		this.processor = processor;
	}
	
	protected Object createInstance() throws Exception {
		RiotFile file = createRiotFile(resource);
		if (processor != null) {
			processor.process(file);
		}
		return file;
	}
	
	protected abstract RiotFile createRiotFile(Resource resource) throws IOException;

}
