/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.io;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * FactoryBean that creates a {@link MimetypesFileTypeMap}.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class MimetypesFileTypeMapFactoryBean
		implements FactoryBean<MimetypesFileTypeMap> {

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

	public MimetypesFileTypeMap getObject() throws Exception {
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

	public Class<MimetypesFileTypeMap> getObjectType() {
		return MimetypesFileTypeMap.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
}
