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

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentMap extends Content {

	private Map contentMap;
	
	public ContentMap() {
	}

	public ContentMap(Map contentMap) {
		this.contentMap = contentMap;
	}
	
	public Object getValue() {
		return contentMap;
	}
	
	public void setValue(Object value) {
		contentMap = (Map) value;
	}
	
	public Content remove(String key) {
		if (contentMap != null) {
			return (Content) contentMap.remove(key);
		}
		return null;
	}
	
	public void clear() {
		if (contentMap != null) {
			contentMap.clear();
		}
	}
	
	public void put(String key, Content value) {
		if (contentMap == null) {
			contentMap = new HashMap();
		}
		contentMap.put(key, value);
	}
	
	public Content get(String key) {
		if (contentMap == null) {
			return null;
		}
		return (Content) contentMap.get(key);
	}
	
	public Object getUwrapped(String key) {
		if (contentMap == null) {
			return null;
		}
		Content content = (Content) contentMap.get(key);
		if (content == null) {
			return null;
		}
		return content.unwrap();
	}

	public Map getUnwrappedMap() {
		if (contentMap == null) {
			return Collections.EMPTY_MAP;
		}
		HashMap result = new HashMap();
		Iterator it = contentMap.entrySet().iterator();
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
		return Collections.unmodifiableMap(result);
	}
	
	public Object unwrap() {
		return getUnwrappedMap();
	}
	
	public Content deepCopy() {
		HashMap copy = new HashMap();
		if (contentMap != null) {
			Iterator it = contentMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Content content = (Content) entry.getValue();
				copy.put(entry.getKey(), content.deepCopy());
			}
		}
		return new ContentMap(copy);
	}
	
	public Collection getCacheTags() {
		if (contentMap == null) {
			return null;
		}
		HashSet result = new HashSet();
		Iterator it = contentMap.values().iterator();
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
