/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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