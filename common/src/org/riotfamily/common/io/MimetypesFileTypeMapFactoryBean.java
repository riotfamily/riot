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
