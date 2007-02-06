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
	
	private VersionContainer container;
	
	public ComponentVersion() {
	}
	
	public ComponentVersion(String type) {
		this.type = type;
	}

	public ComponentVersion(ComponentVersion prototype) {
		if (prototype != null) {
			this.type = prototype.getType();
			this.container = prototype.getContainer();
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
		return container;
	}
	
	public void setContainer(VersionContainer container) {
		this.container = container;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
}
