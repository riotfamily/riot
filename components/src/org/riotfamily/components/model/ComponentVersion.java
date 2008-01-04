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
package org.riotfamily.components.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Versioned model for a component. The component properties are stored as
 * map of Content objects keyed by Strings.
 */
public class ComponentVersion {

	private Long id;

	private Map contents;

	private boolean dirty;

	private VersionContainer container;

	public ComponentVersion() {
	}

	public ComponentVersion(ComponentVersion prototype) {
		if (prototype != null) {
			this.container = prototype.getContainer();
			this.contents = new HashMap();
			Iterator it = prototype.getContents().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Content content = (Content) entry.getValue();
				this.contents.put(entry.getKey(), content.deepCopy());
			}
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

	public Content getContent(String key) {
		return (Content) getContents().get(key);
	}
	
	public void setContent(String key, Content content) {
		getContents().put(key, content);
		setDirty(true);
	}
	
	public Object getValue(String key) {
		if (contents == null) {
			return null;
		}
		Content content = getContent(key);
		return content != null ? content.getValue() : null;
	}
	
	public void setValue(String key, Object value) {
		if (value == null) {
			getContents().remove(key);
		}
		else if (value instanceof Content) {
			setContent(key, (Content) value);
		}
		else {
			setContent(key, ContentFactoryService.createOrUpdateContent(
					getContent(key), value));
		}
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

	public Map getContents() {
		if (contents == null) {
			contents = new HashMap();
		}
		return contents;
	}

	public void setContents(Map contents) {
		this.contents = contents;
	}
	
	public Map getValues() {
		HashMap result = new HashMap();
		if (contents != null) {
			Iterator it = contents.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Content content = (Content) entry.getValue();
				if (content != null) {
					result.put(entry.getKey(), content.unwrap());
				}
				else {
					result.put(entry.getKey(), null);
				}
			}
		}
		return result;
	}
	
	public void setValues(Map values) {
		if (values != null) {
			Iterator it = values.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				Object value = entry.getValue();
				setValue(key, value);
			}
			it = getContents().keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				if (!values.containsKey(key)) {
					it.remove();
				}
			}
		}
		else {
			getContents().clear();
		}
		setDirty(true);
		
	}
	
	/**
	 * Returns a Collection of Strings that should be used to tag the
	 * CacheItem containing the rendered ComponentVersion.
	 */
	public Collection getCacheTags() {
		HashSet result = new HashSet();
		Iterator it = contents.values().iterator();
		while (it.hasNext()) {
			Content content = (Content) it.next();
			if (content != null) {
				Collection tags = content.getCacheTags();
				if (tags != null) {
					result.addAll(tags);
				}
			}
		}
		return result;
	}
	
}
