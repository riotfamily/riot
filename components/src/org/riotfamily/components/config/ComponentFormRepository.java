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
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.property.JSONArrayPropertyProcessor;
import org.riotfamily.components.property.JSONObjectPropertyProcessor;
import org.riotfamily.components.property.PropertyEditorProcessor;
import org.riotfamily.components.property.PropertyProcessorRegistry;
import org.riotfamily.components.property.RiotDaoPropertyProcessor;
import org.riotfamily.forms.ContainerElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.Checkbox;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.element.NumberField;
import org.riotfamily.forms.element.collection.ListEditor;
import org.riotfamily.forms.element.upload.FileUpload;
import org.riotfamily.forms.element.upload.FlashUpload;
import org.riotfamily.forms.element.upload.ImageUpload;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.xml.XmlFormRepository;
import org.riotfamily.riot.form.element.ObjectChooser;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFormRepository extends XmlFormRepository {

	private ComponentRepository componentRepository;

	private ComponentDao componentDao;
	
	private HashMap componentElements;
	
	public ComponentFormRepository(ComponentRepository componentRepository, 
			ComponentDao componentDao) {
		
		this.componentRepository = componentRepository;
		this.componentDao = componentDao;
		componentRepository.setFormRepository(this);
		setDefaultBeanClass(HashMap.class);
	}

	public void configure() {
		componentRepository.resetPropertyProcessors();
		componentElements = new HashMap();
		super.configure();
		registerPropertyProcessors();
	}

	public void registerFormFactory(String id, FormFactory formFactory) {
		String[] s = id.split("#");
		String type = s[0];
		String formId = s[s.length - 1];
		super.registerFormFactory(formId, formFactory);		
	}
	
	private List getElementList(String componentType) {
		List list = (List) componentElements.get(componentType);
		if (list == null) {
			list = new ArrayList();
			componentElements.put(componentType, list);
		}
		return list;
	}
	
	public void registerPropertyProcessors() {
		Iterator it = getFactories().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String type = (String) entry.getKey();
			FormFactory formFactory = (FormFactory) entry.getValue();
			Form form = formFactory.createForm();
			getElementList(type).addAll(form.getElements());
		}
		
		it = componentElements.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String type = (String) entry.getKey();
			List elements = (List) entry.getValue();
			Component component = componentRepository.getComponent(type);
			registerPropertyProcessors(type, component, elements);
		}
	}
	
	private void registerPropertyProcessors(String componentType,
			PropertyProcessorRegistry registry, List elements) {
		
		HashSet processedProperties = new HashSet();
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			Object element = it.next();
			if (element instanceof Editor) {
				Editor editor = (Editor) element;
				String property = editor.getEditorBinding().getProperty();
				if (!processedProperties.contains(property)) {
					registerPropertyProcessors(componentType, registry, editor);
					processedProperties.add(editor);
				}
			}
			else if (element instanceof ContainerElement) {
				ContainerElement ce = (ContainerElement) element;
				registerPropertyProcessors(componentType, registry, ce.getElements());
			}
		}
	}

	private void registerPropertyProcessors(String componentType,
			PropertyProcessorRegistry registry, Editor e) {
		
		String property = e.getEditorBinding() != null 
				? e.getEditorBinding().getProperty() : null;
				
		if (e instanceof FileUpload) {
			registerFileUploadProcessor(componentType, registry, property, (FileUpload) e);
		}
		else if (e instanceof Checkbox) {
			registerCheckboxProcessor(registry, property, (Checkbox) e);
		}
		else if (e instanceof NumberField) {
			registerNumberFieldProcessor(registry, property, (NumberField) e);
		}
		else if (e instanceof NestedForm) {
			registerNestedFormProcessor(componentType, registry, property, (NestedForm) e);
		}
		else if (e instanceof ListEditor) {
			registerListProcessor(componentType, registry, property, (ListEditor) e);
		}
		else if (e instanceof ObjectChooser) {
			registerObjectChooserProcessor(registry, property, (ObjectChooser) e);
		}
	}

	private void registerFileUploadProcessor(String componentType, 
			PropertyProcessorRegistry registry,
			String property, FileUpload upload) {

		/*
		registry.registerPropertyProcessor(property,
				new FileStoreProperyProcessor(upload.getFileStore()));
		*/
		
		componentDao.saveFileStorageInfo(componentType, property, upload.getStore());
		
		if (upload.getSizeProperty() != null) {
			registry.registerPropertyProcessor(upload.getSizeProperty(),
					new PropertyEditorProcessor(
					new CustomNumberEditor(Long.class, true)));
		}
		if (upload instanceof ImageUpload) {
			ImageUpload imageUpload = (ImageUpload) upload;
			if (imageUpload.getWidthProperty() != null) {
				registry.registerPropertyProcessor(
						imageUpload.getWidthProperty(),
						new PropertyEditorProcessor(
						new CustomNumberEditor(Integer.class, true)));
			}
			if (imageUpload.getHeightProperty() != null) {
				registry.registerPropertyProcessor(
						imageUpload.getHeightProperty(),
						new PropertyEditorProcessor(
						new CustomNumberEditor(Integer.class, true)));
			}
		}
		if (upload instanceof FlashUpload) {
			FlashUpload flashUpload = (FlashUpload) upload;
			if (flashUpload.getWidthProperty() != null) {
				registry.registerPropertyProcessor(
						flashUpload.getWidthProperty(),
						new PropertyEditorProcessor(
						new CustomNumberEditor(Integer.class, true)));
			}
			if (flashUpload.getHeightProperty() != null) {
				registry.registerPropertyProcessor(
						flashUpload.getHeightProperty(),
						new PropertyEditorProcessor(
						new CustomNumberEditor(Integer.class, true)));
			}
			if (flashUpload.getVersionProperty() != null) {
				registry.registerPropertyProcessor(
						flashUpload.getVersionProperty(),							new PropertyEditorProcessor(
						new CustomNumberEditor(Integer.class, true)));
			}
		}
	}
	
	private void registerCheckboxProcessor(PropertyProcessorRegistry registry, 
			String property, Checkbox checkbox) {
		
		registry.registerPropertyProcessor(property,
				new PropertyEditorProcessor(
				new BooleanEditor(),
				Boolean.toString(checkbox.isCheckedByDefault())));
	}
	
	private void registerNumberFieldProcessor(
			PropertyProcessorRegistry registry, String property, 
			NumberField numberField) {
		
		Class numberClass = numberField.getEditorBinding().getPropertyType();
		if (!(Number.class.isAssignableFrom(numberClass))) {
			numberClass = Integer.class;
		}
		registry.registerPropertyProcessor(property,
				new PropertyEditorProcessor(
				new CustomNumberEditor(numberClass, false)));
	}
	
	private void registerNestedFormProcessor(String componentType,
			PropertyProcessorRegistry registry,
			String property, NestedForm nestedForm) {
		
		JSONObjectPropertyProcessor pp = new JSONObjectPropertyProcessor();
		pp.setBeanClass(nestedForm.getBeanClass());
		registerPropertyProcessors(componentType, pp, nestedForm.getElements());
		registry.registerPropertyProcessor(property, pp);
	}

	private void registerListProcessor(String componentType,
			PropertyProcessorRegistry registry, 
			String property, ListEditor listEditor) {
		
		JSONArrayPropertyProcessor pp = new JSONArrayPropertyProcessor();
		pp.setCollectionClass(listEditor.getCollectionClass());
		Editor itemEditor = (Editor) listEditor.getItemElementFactory()
				.createElement(listEditor, listEditor.getForm());
		
		registerPropertyProcessors(componentType, pp, itemEditor);
		registry.registerPropertyProcessor(property, pp);
	}
	
	private void registerObjectChooserProcessor(
			PropertyProcessorRegistry registry, String property, 
			ObjectChooser chooser) {
		
		RiotDaoPropertyProcessor pp = new RiotDaoPropertyProcessor(
				chooser.getRiotDao());
		
		registry.registerPropertyProcessor(property, pp);
	}

}
