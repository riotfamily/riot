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
package org.riotfamily.components.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.beans.propertyeditors.BooleanEditor;
import org.riotfamily.components.config.component.Component;
import org.riotfamily.components.property.FileStoreProperyProcessor;
import org.riotfamily.components.property.PropertyEditorProcessor;
import org.riotfamily.components.property.XmlPropertyProcessor;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.Checkbox;
import org.riotfamily.forms.element.NumberField;
import org.riotfamily.forms.element.collection.XmlSequence;
import org.riotfamily.forms.element.upload.FileUpload;
import org.riotfamily.forms.element.upload.FlashUpload;
import org.riotfamily.forms.element.upload.ImageUpload;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.xml.XmlFormRepository;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFormRepository extends XmlFormRepository {

	private ComponentRepository componentRepository;

	private HashMap componentElements;
	
	public ComponentFormRepository(ComponentRepository componentRepository) {
		this.componentRepository = componentRepository;
		componentRepository.setFormRepository(this);
		setDefaultBeanClass(HashMap.class);
	}

	public void configure() {
		componentRepository.resetPropertyProcessors();
		componentElements = new HashMap();
		super.configure();
		registerPropertyProcessors();
		componentElements = null;
	}

	public void registerFormFactory(String id, FormFactory formFactory) {
		String[] s = id.split("#");
		String type = s[0];
		String formId = s[s.length - 1];
		super.registerFormFactory(formId, formFactory);
		Component component = componentRepository.getComponent(type);
		Form form = formFactory.createForm();
		getElementList(component).addAll(form.getRegisteredElements());
	}
	
	private List getElementList(Component component) {
		List list = (List) componentElements.get(component);
		if (list == null) {
			list = new ArrayList();
			componentElements.put(component, list);
		}
		return list;
	}
	
	private void registerPropertyProcessors() {
		Iterator it = componentElements.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Component component = (Component) entry.getKey();
			List elements = (List) entry.getValue();
			registerPropertyProcessors(component, elements);
		}
	}
	
	private void registerPropertyProcessors(Component component, List elements) {
		HashSet processedProperties = new HashSet();
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			Object element = it.next();
			if (element instanceof Editor) {
				Editor editor = (Editor) element;
				String property = editor.getEditorBinding().getProperty();
				if (!processedProperties.contains(property)) {
					registerPropertyProcessors(component, editor);
					processedProperties.add(editor);
				}
			}
		}
	}

	private void registerPropertyProcessors(Component component, Element e) {
		if (e instanceof FileUpload) {
			FileUpload upload = (FileUpload) e;
			component.addPropertyProcessor(
					new FileStoreProperyProcessor(
					upload.getEditorBinding().getProperty(),
					upload.getFileStore()));

			if (upload.getSizeProperty() != null) {
				component.addPropertyProcessor(
						new PropertyEditorProcessor(
						upload.getSizeProperty(),
						new CustomNumberEditor(Long.class, true)));
			}
			if (upload instanceof ImageUpload) {
				ImageUpload imageUpload = (ImageUpload) upload;
				if (imageUpload.getWidthProperty() != null) {
					component.addPropertyProcessor(
							new PropertyEditorProcessor(
							imageUpload.getWidthProperty(),
							new CustomNumberEditor(Integer.class, true)));
				}
				if (imageUpload.getHeightProperty() != null) {
					component.addPropertyProcessor(
							new PropertyEditorProcessor(
							imageUpload.getHeightProperty(),
							new CustomNumberEditor(Integer.class, true)));
				}
			}
			if (upload instanceof FlashUpload) {
				FlashUpload flashUpload = (FlashUpload) upload;
				if (flashUpload.getWidthProperty() != null) {
					component.addPropertyProcessor(
							new PropertyEditorProcessor(
							flashUpload.getWidthProperty(),
							new CustomNumberEditor(Integer.class, true)));
				}
				if (flashUpload.getHeightProperty() != null) {
					component.addPropertyProcessor(
							new PropertyEditorProcessor(
							flashUpload.getHeightProperty(),
							new CustomNumberEditor(Integer.class, true)));
				}
				if (flashUpload.getVersionProperty() != null) {
					component.addPropertyProcessor(
							new PropertyEditorProcessor(
							flashUpload.getVersionProperty(),
							new CustomNumberEditor(Integer.class, true)));
				}
			}
		}
		else if (e instanceof Checkbox) {
			Checkbox cb = (Checkbox) e;
			component.addPropertyProcessor(
					new PropertyEditorProcessor(
					cb.getEditorBinding().getProperty(),
					new BooleanEditor(),
					Boolean.toString(cb.isCheckedByDefault())));
		}
		else if (e instanceof NumberField) {
			NumberField nf = (NumberField) e;
			Class numberClass = nf.getEditorBinding().getPropertyType();
			if (!(Number.class.isAssignableFrom(numberClass))) {
				numberClass = Integer.class;
			}
			component.addPropertyProcessor(
					new PropertyEditorProcessor(
					nf.getEditorBinding().getProperty(),
					new CustomNumberEditor(numberClass, false)));
		}
		else if (e instanceof XmlSequence) {
			XmlSequence xs = (XmlSequence) e;
			component.addPropertyProcessor(new XmlPropertyProcessor(
					xs.getEditorBinding().getProperty()));
		}
	}

}
