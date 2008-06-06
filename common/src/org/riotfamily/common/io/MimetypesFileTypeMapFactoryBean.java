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
package org.riotfamily.common.io;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * FactoryBean that creates a {@link MimetypesFileTypeMap}.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class MimetypesFileTypeMapFactoryBean implements FactoryBean {

	private Resource location;
	
	private MimetypesFileTypeMap map;
	
	private String[] types;
	
	/**
	 * Sets a resource containing mappings in the <tt>.mime.types</tt> format. 
	 */
	public void setLocation(Resource location) {
		this.location = location;
	}

	/**
	 * Sets mappings in the <tt>.mime.types</tt> format that are 
	 * programatically added to the FileTypesMap.
	 */
	public void setTypes(String[] types) {
		this.types = types;
	}

	public Object getObject() throws Exception {
		if (map == null) {
			if (location != null) {
				map = new MimetypesFileTypeMap(location.getInputStream());
			}
			else {
				map = new MimetypesFileTypeMap();
			}
			if (types != null) {
				for (int i = 0; i < types.length; i++) {
					map.addMimeTypes(types[i]);
				}
			}
		}
		return map;
	}

	public Class<?> getObjectType() {
		return MimetypesFileTypeMap.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
}
