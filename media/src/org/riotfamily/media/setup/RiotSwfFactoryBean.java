package org.riotfamily.media.setup;

import java.io.IOException;

import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotSwf;
import org.springframework.core.io.Resource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotSwfFactoryBean extends AbstractRiotFileFactoryBean {
	
	public Class<?> getObjectType() {
		return RiotSwf.class;
	}
	
	protected RiotFile createRiotFile(Resource resource) throws IOException {
		return new RiotSwf(resource.getFile());
	}
}
