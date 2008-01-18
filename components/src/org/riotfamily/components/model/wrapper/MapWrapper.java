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
package org.riotfamily.components.model.wrapper;

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

	private Map wrapperMap;
	
	public MapWrapper() {
	}

	public void wrap(Object value) {
		putAll((Map) value);
	}
	
	public Object getValue() {
		return this;
	}
	
	public void setValue(Object value) {
	}
	
	public ValueWrapper getWrapper(String key) {
		if (wrapperMap != null) {
			return (ValueWrapper) wrapperMap.get(key);
		}
		return null;
	}
	
	public Object unwrap() {
		if (wrapperMap == null) {
			return Collections.EMPTY_MAP;
		}
		HashMap result = new HashMap();
		Iterator it = wrapperMap.entrySet().iterator();
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
		return Collections.unmodifiableMap(result);
	}
	
	public ValueWrapper deepCopy() {
		HashMap map = new HashMap();
		if (wrapperMap != null) {
			Iterator it = wrapperMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				ValueWrapper wrapper = (ValueWrapper) entry.getValue();
				map.put(entry.getKey(), wrapper.deepCopy());
			}
		}
		MapWrapper copy = new MapWrapper();
		copy.wrap(map);
		return copy;
	}
	
	public Collection getCacheTags() {
		if (wrapperMap == null) {
			return null;
		}
		HashSet result = new HashSet();
		Iterator it = wrapperMap.values().iterator();
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

	public void clear() {
		if (wrapperMap != null) {
			wrapperMap.clear();
		}
	}

	public boolean containsKey(Object key) {
		return wrapperMap != null && wrapperMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return wrapperMap != null && wrapperMap.containsValue(value);
	}

	public Set keySet() {
		if (wrapperMap == null) {
			return Collections.EMPTY_SET;
		}
		return wrapperMap.keySet();
	}
	
	public Set entrySet() {
		if (wrapperMap == null) {
			return Collections.EMPTY_SET;
		}
		return wrapperMap.entrySet();
	}
	
	public Collection values() {
		if (wrapperMap == null) {
			return Collections.EMPTY_SET;
		}
		return wrapperMap.values();
	}
	
	public boolean isEmpty() {
		return wrapperMap == null || wrapperMap.isEmpty();
	}
	
	public int size() {
		if (wrapperMap == null) {
			return 0;
		}
		return wrapperMap.size();
	}
	
	public Object remove(Object key) {
		if (wrapperMap != null) {
			return wrapperMap.remove(key);
		}
		return null;
	}

	public Object get(Object key) {
		if (wrapperMap != null) {
			ValueWrapper wrapper = (ValueWrapper) wrapperMap.get(key);
			if (wrapper != null) {
				return wrapper.getValue();
			}
		}
		return null;
	}

	public Object put(Object key, Object value) {
		if (value == null) {
			return remove(key);
		}
		if (wrapperMap == null) {
			wrapperMap = new HashMap();
		}
		if (value instanceof ValueWrapper) {
			return wrapperMap.put(key, value);
		}
		ValueWrapper oldValue = (ValueWrapper) wrapperMap.get(key);
		wrapperMap.put(key, ValueWrapperService.createOrUpdate(oldValue, value));
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
