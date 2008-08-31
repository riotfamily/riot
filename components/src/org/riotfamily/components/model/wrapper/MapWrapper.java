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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.MapKey;
import org.riotfamily.common.util.Generics;
import org.springframework.util.ObjectUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@DiscriminatorValue("Map")
public class MapWrapper extends ValueWrapper<Map<String, Object>> 
		implements Map<String, Object> {

	private Map<String, Object> wrapperMap;
		
	@OneToMany(targetEntity=ValueWrapper.class)
	@JoinColumn(name="map_value")
	@MapKey(columns={@Column(name="map_key")})
	@Cascade(CascadeType.ALL)
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
	public Map<String, Object> getWrapperMap() {
		return wrapperMap;
	}

	public void setWrapperMap(Map<String, Object> wrapperMap) {
		this.wrapperMap = wrapperMap;
	}


	public void wrap(Object value) {
		putAll((Map<String, Object>) value);
	}
	
	@Transient
	public Map<String, Object> getValue() {
		return this;
	}
	
	public void setValue(Map<String, Object> value) {
	}
	
	public ValueWrapper<?> getWrapper(String key) {
		if (wrapperMap != null) {
			return (ValueWrapper<?>) wrapperMap.get(key);
		}
		return null;
	}
	
	public Map<String, Object> unwrap() {
		if (wrapperMap == null) {
			return Collections.emptyMap();
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : wrapperMap.entrySet()) {
			ValueWrapper<?> wrapper = (ValueWrapper<?>) entry.getValue();
			if (wrapper != null) {
				result.put(entry.getKey(), wrapper.unwrap());
			}
			else {
				result.put(entry.getKey(), null);
			}
		}
		return Collections.unmodifiableMap(result);
	}
	
	public MapWrapper deepCopy() {
		HashMap<String, Object> map = Generics.newHashMap();
		if (wrapperMap != null) {
			Iterator<Map.Entry<String, Object>> it = wrapperMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				ValueWrapper<?> wrapper = (ValueWrapper<?>) entry.getValue();
				map.put(entry.getKey(), wrapper.deepCopy());
			}
		}
		MapWrapper copy = new MapWrapper();
		copy.wrap(map);
		return copy;
	}
	
	@Transient
	public Collection<String> getCacheTags() {
		if (wrapperMap == null) {
			return null;
		}
		HashSet<String> result = new HashSet<String>();
		Iterator<Object> it = wrapperMap.values().iterator();
		while (it.hasNext()) {
			ValueWrapper<?> wrapper = (ValueWrapper<?>) it.next();
			if (wrapper != null) {
				Collection<String> tags = wrapper.getCacheTags();
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

	public Set<String> keySet() {
		if (wrapperMap == null) {
			return Collections.emptySet();
		}
		return wrapperMap.keySet();
	}
	
	public Set<Map.Entry<String, Object>> entrySet() {
		if (wrapperMap == null) {
			return Collections.emptySet();
		}
		return wrapperMap.entrySet();
	}
	
	public Collection<Object> values() {
		if (wrapperMap == null) {
			return Collections.emptySet();
		}
		return wrapperMap.values();
	}
	
	@Transient
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
			ValueWrapper<?> wrapper = (ValueWrapper<?>) wrapperMap.get(key);
			if (wrapper != null) {
				return wrapper.getValue();
			}
		}
		return null;
	}

	public Object put(String key, Object value) {
		if (value == null) {
			return remove(key);
		}
		if (wrapperMap == null) {
			wrapperMap = new HashMap<String, Object>();
		}
		if (value instanceof ValueWrapper) {
			return wrapperMap.put(key, value);
		}
		ValueWrapper<Object> oldValue = (ValueWrapper<Object>) wrapperMap.get(key);
		wrapperMap.put(key, ValueWrapperService.createOrUpdate(oldValue, value));
		return oldValue;
	}

	public void putAll(Map<? extends String, ?> map) {
		Iterator<? extends Entry<? extends String, ?>> i = map.entrySet().iterator();
		while (i.hasNext()) {
		    Entry<? extends String, ?> e = i.next();
		    put(e.getKey(), e.getValue());
		}
	}
	
	@Override
	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		}
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MapWrapper) {
			if (getId() != null) {
				MapWrapper other = (MapWrapper) obj;
				return getId().equals(other.getId());
			}
			return super.equals(obj);
		}
		return false;
	}

}
