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
package org.riotfamily.forms;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.beans.propertyeditors.SqlDateEditor;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.propertyeditors.CustomDateEditor;


/**
 * This class is used to bind a form element to the property of a bean. The
 * form element must implement the {@link org.riotfamily.forms.Editor}
 * interface.
 */
public abstract class AbstractEditorBinder extends PropertyEditorRegistrySupport
		implements EditorBinder {

	private Log log = LogFactory.getLog(AbstractEditorBinder.class);

	/** List of {@link EditorBinding editor bindings} */
	private List bindings = new LinkedList();

	public AbstractEditorBinder() {
		registerDefaultEditors();
		registerCustomEditor(java.sql.Date.class,new SqlDateEditor());
		registerCustomEditor(Date.class, new CustomDateEditor(
				new SimpleDateFormat("yyyy-MM-dd"), false));
	}
	
	public abstract Class getPropertyType(String path);
	
	public List getBindings() {
		return this.bindings;
	}

	public EditorBinder replace(EditorBinder previousBinder) {
		if (previousBinder != null) {
			this.bindings = previousBinder.getBindings();
		}
		return this;
	}

	public void bind(Editor editor, String property) {
		log.debug("Binding " + editor + " to property " + property);
		EditorBinding eb = new EditorBindingImpl(editor, property);
		bindings.add(eb);
		editor.setEditorBinding(eb);
	}

	public Editor getEditor(String property) {
		if (property != null) {
			int i = property.indexOf('.');
			if (i != -1) {
				String nested = property.substring(i + 1);
				property = property.substring(0, i);
				Editor editor = findEditorByProperty(property);
				if (editor instanceof BeanEditor) {
					BeanEditor be = (BeanEditor) editor;
					return be.getEditor(nested);
				}
				else {
					throw new InvalidPropertyException(getBeanClass(),
							property, "Nested editor must implement the " +
							"BeanEditor interface");
				}
			}
			return findEditorByProperty(property);
		}
		return null;
	}

	protected Editor findEditorByProperty(String property) {
		Iterator it = bindings.iterator();
		while (it.hasNext()) {
			EditorBinding binding = (EditorBinding) it.next();
			if (property.equals(binding.getProperty())) {
				return binding.getEditor();
			}
		}
		throw new InvalidPropertyException(getBeanClass(), property,
				"No editor bound to property");
	}

	public String[] getBoundProperties() {
		String[] props = new String[bindings.size()];
		Iterator it = bindings.iterator();
		for (int i = 0; it.hasNext(); i++) {
			EditorBinding binding = (EditorBinding) it.next();
			props[i] = binding.getProperty();
		}
		return props;
	}

	public void registerPropertyEditors(PropertyEditorRegistrar[] registrars) {
		if (registrars != null) {
			for (int i = 0; i < registrars.length; i++) {
				registrars[i].registerCustomEditors(this);
			}
		}
	}

	public void initEditors() {
		Iterator it = bindings.iterator();
		while (it.hasNext()) {
			EditorBinding binding = (EditorBinding) it.next();
			Editor editor = binding.getEditor();
			Object value = null;
			if (isEditingExistingBean()) {
				value = getPropertyValue(binding.getProperty());
			}
			editor.setValue(value);
		}
	}

	public Object populateBackingObject() {
		Iterator it = bindings.iterator();
		while (it.hasNext()) {
			EditorBinding binding = (EditorBinding) it.next();
			Object value = binding.getEditor().getValue();
			if (binding.getEditor().isEnabled()) {
				setPropertyValue(binding.getProperty(), value);
			}
		}
		return getBackingObject();
	}

	public PropertyEditor getPropertyEditor(Class type, String propertyPath) {
		PropertyEditor pe = findCustomEditor(type, propertyPath);
		if (pe == null) {
			pe = getDefaultEditor(type);
		}
		return pe;
	}
	
	private class EditorBindingImpl implements EditorBinding {

		private Editor editor;

		private String property;
		
		public EditorBindingImpl(Editor editor,	String property) {
			this.editor = editor;
			this.property = property;
		}

		public EditorBinder getEditorBinder() {
			return AbstractEditorBinder.this;
		}

		public void setEditor(Editor editor) {
			this.editor = editor;
		}
		
		public Editor getEditor() {
			return editor;
		}

		public String getProperty() {
			return property;
		}
		
		public Object getValue() {
			return getPropertyValue(property);
		}
		
		public Class getBeanClass() {
			return AbstractEditorBinder.this.getBeanClass();
		}
		
		public boolean isEditingExistingBean() {
			return AbstractEditorBinder.this.isEditingExistingBean();
		}
		
		public String getPropertyPath() {
			EditorBinding parentBinding = findParentBinding();
			if (parentBinding != null) {
				return parentBinding.getPropertyPath() + '.' + getProperty();
			}
			return getProperty();
		}
		
		private EditorBinding findParentBinding() {
			Element parent = editor.getParent();
			while (parent != null) {
				if (parent instanceof Editor) {
					Editor parentEditor = (Editor) parent;
					if (parentEditor.getEditorBinding() != null) {
						return parentEditor.getEditorBinding(); 
					}
				}
				parent = parent.getParent();
			}
			return null;
		}
		
		public Class getPropertyType() {
			return AbstractEditorBinder.this.getPropertyType(property);
		}
		
		public PropertyEditor getPropertyEditor() {
			return AbstractEditorBinder.this.getPropertyEditor(
					getPropertyType(), getPropertyPath());
		}
		
	}

}
