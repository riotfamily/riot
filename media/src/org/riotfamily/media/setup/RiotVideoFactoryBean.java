package org.riotfamily.media.setup;

import java.io.IOException;

import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotVideo;
import org.springframework.core.io.Resource;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 7.0
 */
public class RiotVideoFactoryBean extends AbstractRiotFileFactoryBean {
	
	public Class<?> getObjectType() {
		return RiotVideo.class;
	}
	
	protected RiotFile createRiotFile(Resource resource) throws IOException {
		return new RiotVideo(resource.getFile());
	}

}
