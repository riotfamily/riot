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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MapWrapper extends ValueWrapper implements Map {

	private Map contentMap;
	
	public MapWrapper() {
	}

	public MapWrapper(Map map) {
		putAll(map);
	}
	
	public Object getValue() {
		return this;
	}
	
	public void setValue(Object value) {
		//Assert.isTrue(value == this);
	}
	
	public ValueWrapper getContent(String key) {
		if (contentMap != null) {
			return (ValueWrapper) contentMap.get(key);
		}
		return null;
	}
	
	public Object unwrap() {
		if (contentMap == null) {
			return Collections.EMPTY_MAP;
		}
		HashMap result = new HashMap();
		Iterator it = contentMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			ValueWrapper content = (ValueWrapper) entry.getValue();
			if (content != null) {
				result.put(entry.getKey(), content.unwrap());
			}
			else {
				result.put(entry.getKey(), null);
			}
		}
		return Collections.unmodifiableMap(result);
	}
	
	public ValueWrapper deepCopy() {
		HashMap copy = new HashMap();
		if (contentMap != null) {
			Iterator it = contentMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				ValueWrapper content = (ValueWrapper) entry.getValue();
				copy.put(entry.getKey(), content.deepCopy());
			}
		}
		return new MapWrapper(copy);
	}
	
	public Collection getCacheTags() {
		if (contentMap == null) {
			return null;
		}
		HashSet result = new HashSet();
		Iterator it = contentMap.values().iterator();
		while (it.hasNext()) {
			ValueWrapper content = (ValueWrapper) it.next();
			if (content != null) {
				Collection tags = content.getCacheTags();
				if (tags != null) {
					result.addAll(tags);
				}
			}
		}
		return result;
	}

	public void clear() {
		if (contentMap != null) {
			contentMap.clear();
		}
	}

	public boolean containsKey(Object key) {
		return contentMap != null && contentMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return contentMap != null && contentMap.containsValue(value);
	}

	public Set keySet() {
		if (contentMap == null) {
			return Collections.EMPTY_SET;
		}
		return contentMap.keySet();
	}
	
	public Set entrySet() {
		if (contentMap == null) {
			return Collections.EMPTY_SET;
		}
		return contentMap.entrySet();
	}
	
	public Collection values() {
		if (contentMap == null) {
			return Collections.EMPTY_SET;
		}
		return contentMap.values();
	}
	
	public boolean isEmpty() {
		return contentMap == null || contentMap.isEmpty();
	}
	
	public int size() {
		if (contentMap == null) {
			return 0;
		}
		return contentMap.size();
	}
	
	public Object remove(Object key) {
		if (contentMap != null) {
			return contentMap.remove(key);
		}
		return null;
	}

	public Object get(Object key) {
		if (contentMap != null) {
			ValueWrapper content = (ValueWrapper) contentMap.get(key);
			if (content != null) {
				return content.getValue();
			}
		}
		return null;
	}

	public Object put(Object key, Object value) {
		if (value == null) {
			return remove(key);
		}
		if (contentMap == null) {
			contentMap = new HashMap();
		}
		if (value instanceof ValueWrapper) {
			return contentMap.put(key, value);
		}
		ValueWrapper oldValue = (ValueWrapper) contentMap.get(key);
		contentMap.put(key, ValueWrapperService.createOrUpdate(oldValue, value));
		return oldValue;
	}

	public void putAll(Map map) {
		if (map != null) {
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				put(entry.getKey(), entry.getValue());
			}
		}
	}

}
