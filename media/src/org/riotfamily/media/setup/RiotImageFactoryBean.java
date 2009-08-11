package org.riotfamily.media.setup;

import java.io.IOException;

import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotImage;
import org.springframework.core.io.Resource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotImageFactoryBean extends AbstractRiotFileFactoryBean {
		
	public Class<?> getObjectType() {
		return RiotImage.class;
	}
	
	protected RiotFile createRiotFile(Resource resource) throws IOException {
		return new RiotImage(resource.getFile());
	}
	
}
