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
package org.riotfamily.forms.element.select;

/**
 *
 */
public class Option {

	private Object object;
	
	private Object value;

	private String label;

	private SelectElement parent;
	
	public Option(Object object, Object value, String label, SelectElement parent) {
		this.object = object;
		this.value = value;
		this.label = label;
		this.parent = parent;
	}

	public SelectElement getParent() {
		return parent;
	}
	
	public boolean isSelected() {
		return parent.isSelected(this);
	}

	public int getIndex() {
		return parent.getOptionIndex(this);
	}

	public String getLabel() {
		return label;
	}

	public Object getValue() {
		return value;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public String getId() {
		return parent.getId() + '-' + getIndex();
	}

	public void render() {
		parent.renderOption(this);
	}

}