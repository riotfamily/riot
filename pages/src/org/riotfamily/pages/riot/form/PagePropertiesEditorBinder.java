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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.riot.form.ContentContainerEditorBinder;
import org.riotfamily.forms.EditorBinding;
import org.riotfamily.pages.model.PageProperties;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertiesEditorBinder extends ContentContainerEditorBinder {

	private Map elements = new HashMap();
	
	public Class getBeanClass() {
		return PageProperties.class;
	}
	
	protected ContentContainer createContainer() {
		return new PageProperties();
	}
	
	public void registerElement(EditorBinding binding, 
			PagePropertyElement editor) {
		
		elements.put(binding, editor);
	}
	
	private boolean isOverwrite(EditorBinding binding) {
		PagePropertyElement ele = (PagePropertyElement) elements.get(binding);
		return ele.isOverwrite();
	}
	
	private Object getValue(EditorBinding binding) {
		return isOverwrite(binding) ? binding.getEditor().getValue() : null;
	}
	
	public Object populateBackingObject() {
		Iterator it = getBindings().iterator();
		while (it.hasNext()) {
			EditorBinding binding = (EditorBinding) it.next();
			setPropertyValue(binding.getProperty(), getValue(binding));
		}
		return getBackingObject();
	}
	
}
