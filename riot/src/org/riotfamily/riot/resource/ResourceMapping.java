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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.resource;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

public class ResourceMapping {

	private Log log = LogFactory.getLog(ResourceMapping.class);
	
	private String path;
	
	private Resource location;

	private boolean skip;
	
	public Resource getLocation() {
		return this.location;
	}

	public void setLocation(Resource location) {
		this.location = location;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Resource getResource(String path) throws IOException {
		if (!skip && path.startsWith(this.path)) {
			String relativePath = path.substring(this.path.length());
			Resource res = location.createRelative(relativePath);
			log.debug("Looking for resource: " + res);
			if (res.exists()) {
				return res;
			}
		}
		return null;
	}

}
