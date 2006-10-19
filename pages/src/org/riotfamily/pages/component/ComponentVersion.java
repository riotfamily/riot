package org.riotfamily.pages.component;

import java.util.HashMap;
import java.util.Map;

/**
 * Versioned model for a component. The component properties are stored as
 * map of Strings, additionally a type information is included which is used
 * to look up a {@link Component} from the {@link ComponentRepository}.  
 */
public class ComponentVersion {

	private Long id;
	
	private String type;
	
	private Map properties;
	
	private boolean dirty;
	
	private VersionContainer liveContainer;
	
	private VersionContainer previewContainer;
		
	public ComponentVersion() {
	}
	
	public ComponentVersion(String type) {
		this.type = type;
	}

	public ComponentVersion(ComponentVersion prototype) {
		if (prototype != null) {
			this.type = prototype.getType();
			this.liveContainer = prototype.getContainer();
			this.previewContainer = prototype.getContainer();
			this.properties = new HashMap(prototype.getProperties());
		}
	}

	/**
	 * Returns the entity's id set via {@link #setId(Long)}.
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the entity's id.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the component-type. The String returned by this method is used
	 * to lookup a component implementation from the ComponentRepository.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the component-type.
	 */
	public void setType(String type) {
		this.type = type;
		setDirty(true);
	}

	public Map getProperties() {
		if (properties == null) {
			properties = new HashMap();
		}
		return properties;
	}

	public void setProperties(Map properties) {
		this.properties = properties;
	}

	public String getProperty(String key) {
		if (properties == null) {
			return null;
		}
		return (String) properties.get(key);
	}
	
	public void setProperty(String key, String text) {
		getProperties().put(key, text);
		setDirty(true);
	}

	/**
	 * Returns the VersionContainer this version belongs to.
	 */
	public VersionContainer getContainer() {
		return previewContainer != null ? previewContainer : liveContainer;
	}
	
	/*
	public void setContainer(VersionContainer container) {
		this.liveContainer = container;
		this.previewContainer = container;
	}
	*/

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
}
