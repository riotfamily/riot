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
 *   flx
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.collection;

import java.beans.PropertyEditor;

import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.EditorBinding;



public class CollectionItemEditorBinding implements EditorBinding {

		private EditorBinder binder;
		
		private Editor editor;
		
		private Object value;
		
		private boolean existingItem;
		
		
		public CollectionItemEditorBinding(EditorBinder binder) {
			this.binder = binder;
		}

		public Class<?> getBeanClass() {
			return value != null ? value.getClass() : Object.class;
		}

		public boolean isEditingExistingBean() {
			return existingItem;
		}
		
		public void setExistingItem(boolean existingItem) {
			this.existingItem = existingItem;
		}
		
		public void setEditor(Editor editor) {
			this.editor = editor;
		}
		
		public Editor getEditor() {
			return editor;
		}

		public EditorBinder getEditorBinder() {
			return binder;
		}

		public String getProperty() {
			return null;
		}

		public PropertyEditor getPropertyEditor() {
			return binder.getPropertyEditor(getBeanClass(), null);
		}

		public String getPropertyPath() {
			return "[" + editor.getId() + "]";
		}

		public Class<?> getPropertyType() {
			return Object.class;
		}

		public Object getValue() {
			return value;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
		
		
	}