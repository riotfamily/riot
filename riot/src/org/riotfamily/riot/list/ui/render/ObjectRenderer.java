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
package org.riotfamily.riot.list.ui.render;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.io.PrintWriter;

import org.springframework.web.util.HtmlUtils;

/**
 *
 */
public class ObjectRenderer implements CellRenderer {

	private static PropertyEditor DEFAULT_EDITOR = new StringPropertyEditor();
	
	private PropertyEditor propertyEditor;
	
	/**
	 * @param propertyEditor The propertyEditor to use.
	 */
	public void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}
	
	public void render(String propertyName, Object value, RenderContext context, PrintWriter writer) {
		if (value != null) {
			Class type = value.getClass();
			if (propertyEditor == null) {
				propertyEditor = PropertyEditorManager.findEditor(type);
				if (propertyEditor == null) {
					propertyEditor = DEFAULT_EDITOR;
				}
			}
			propertyEditor.setValue(value);
			renderValue(context, writer, propertyEditor.getAsText());
		}
	}

	protected void renderValue(RenderContext context, PrintWriter writer, 
			String value) {
		
		writer.print(HtmlUtils.htmlEscape(value));
	}
	
	private static class StringPropertyEditor extends PropertyEditorSupport {
	}

}
