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
package org.riotfamily.components.property;

import java.util.Map;

import org.riotfamily.components.PropertyProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Abstract base class for PropertyProcessors that process a single property.
 */
public abstract class AbstractSinglePropertyProcessor 
		implements PropertyProcessor, InitializingBean {

	private String property;
	
	public AbstractSinglePropertyProcessor() {
	}

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(property, "The name of the property must be set.");
		initialize();
	}
	
	protected void initialize() {
	}
	
	public void setProperty(String property) {
		this.property = property;
	}

	public void resolveStrings(Map map) {
		Object value = map.get(property);
		if (value instanceof String || value == null) {
			map.put(property, resolveString((String) value));
		}
	}
	
	protected abstract Object resolveString(String s);
	
	public void convertToStrings(Map map) {
		Object object = map.get(property);
		if (!(object instanceof String)) {
			map.put(property, convertToString(object));
		}
	}

	protected abstract String convertToString(Object object);
	
	public void copy(Map source, Map dest) {
		String s = (String) source.get(property);
		dest.put(property, copy(s));
	}
	
	protected String copy(String s) {
		return s;
	}

	public void delete(Map map) {
		String s = (String) map.get(property);
		delete(s);
	}
	
	protected void delete(String s) {
	}
	
	public String[] getCacheTags(Map map) {
		String s = (String) map.get(property);
		String tag = getCacheTag(s);
		return tag != null ? new String[] {tag} : null;
	}
	
	protected String getCacheTag(String s) {
		return null;
	}

}
