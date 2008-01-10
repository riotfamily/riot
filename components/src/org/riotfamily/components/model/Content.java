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

public class Content {

	private Long id;

	private Map wrappers;

	private boolean dirty;

	private ContentContainer container;

	public Content() {
	}

	public Content(Content prototype) {
		if (prototype != null) {
			this.container = prototype.getContainer();
			this.wrappers = new HashMap();
			Iterator it = prototype.getWrappers().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				ValueWrapper wrapper = (ValueWrapper) entry.getValue();
				this.wrappers.put(entry.getKey(), wrapper.deepCopy());
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

	public ValueWrapper getWrapper(String key) {
		return (ValueWrapper) getWrappers().get(key);
	}
	
	public void setWrapper(String key, ValueWrapper wrapper) {
		getWrappers().put(key, wrapper);
		setDirty(true);
	}
	
	public Object getValue(String key) {
		if (wrappers == null) {
			return null;
		}
		ValueWrapper wrapper = getWrapper(key);
		return wrapper != null ? wrapper.getValue() : null;
	}
	
	public void setValue(String key, Object value) {
		if (value == null) {
			getWrappers().remove(key);
		}
		else if (value instanceof ValueWrapper) {
			setWrapper(key, (ValueWrapper) value);
		}
		else {
			setWrapper(key, ValueWrapperService.createOrUpdate(
					getWrapper(key), value));
		}
		setDirty(true);
	}

	/**
	 * Returns the VersionContainer this version belongs to.
	 */
	public ContentContainer getContainer() {
		return container;
	}

	public void setContainer(ContentContainer container) {
		this.container = container;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public Map getWrappers() {
		if (wrappers == null) {
			wrappers = new HashMap();
		}
		return wrappers;
	}

	public void setWrappers(Map contents) {
		this.wrappers = contents;
	}
	
	public Map getValues() {
		HashMap result = new HashMap();
		if (wrappers != null) {
			Iterator it = wrappers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				ValueWrapper wrapper = (ValueWrapper) entry.getValue();
				if (wrapper != null) {
					result.put(entry.getKey(), wrapper.unwrap());
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
			it = getWrappers().keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				if (!values.containsKey(key)) {
					it.remove();
				}
			}
		}
		else {
			getWrappers().clear();
		}
		setDirty(true);
		
	}
	
	/**
	 * Returns a Collection of Strings that should be used to tag the
	 * CacheItem containing the rendered ComponentVersion.
	 */
	public Collection getCacheTags() {
		HashSet result = new HashSet();
		Iterator it = wrappers.values().iterator();
		while (it.hasNext()) {
			ValueWrapper wrapper = (ValueWrapper) it.next();
			if (wrapper != null) {
				Collection tags = wrapper.getCacheTags();
				if (tags != null) {
					result.addAll(tags);
				}
			}
		}
		return result;
	}
	
}
