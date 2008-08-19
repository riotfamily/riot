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
package org.riotfamily.riot.dao;

import java.io.Serializable;

import org.springframework.beans.support.SortDefinition;

public class Order implements Serializable, SortDefinition {

	String property;

	boolean ascending;
	
	boolean caseSensitive;

	
	public Order(String property, boolean ascending, boolean caseSensitive) {
		this.property = property;
		this.ascending = ascending;
		this.caseSensitive = caseSensitive;
	}

	public String getProperty() {
		return property;
	}

	public boolean isAscending() {
		return ascending;
	}	
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isIgnoreCase() {
		return !caseSensitive;
	}
	
	public boolean isProperty(String property) {
		return this.property.equals(property);
	}
	
	public void toggleDirection() {
		ascending = !ascending;
	}
	
}