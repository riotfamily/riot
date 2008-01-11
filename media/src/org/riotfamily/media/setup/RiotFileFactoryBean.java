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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.media.setup;

import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.data.FileData;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotFileFactoryBean extends AbstractFactoryBean {

	private Resource resource;
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public Class getObjectType() {
		return RiotFile.class;
	}
	
	protected Object createInstance() throws Exception {
		return new RiotFile(new FileData(resource.getFile()));
	}
}
