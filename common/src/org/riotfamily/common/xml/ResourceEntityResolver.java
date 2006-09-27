package org.riotfamily.common.xml;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * EntityResolver that resolves an entity to a 
 * {@link org.springframework.core.io.Resource resource}. This class is 
 * mainly intended to include local DTDs. 
 */
public class ResourceEntityResolver implements EntityResolver {

	private String systemId;
	
	private Resource resource;

	private Log log = LogFactory.getLog(ResourceEntityResolver.class);

	public ResourceEntityResolver(Resource resource) {
		this.resource = resource;
	}
	
	public ResourceEntityResolver(Resource resource, String systemId) {
		this.resource = resource;
		this.systemId = systemId;
	}
	
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	protected boolean resourceMatches(String systemId) {
		if (this.systemId != null) {
			return this.systemId.equals(systemId);
		}
		else {
			return systemId.endsWith(resource.getFilename());
		}
	}
	
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("Trying to resolve XML entity with public ID [" 
					+ publicId + "] and system ID [" + systemId + "]");
		}
		if (resourceMatches(systemId)) {
			try {
				InputSource source = new InputSource(resource.getInputStream());
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				return source;
			}
			catch (IOException ex) {
				log.debug("Could not resolve DTD [" + systemId + "]", ex);
			}
		}
		// use the default behaviour -> download from website or wherever
		return null;
	}
}