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
package org.riotfamily.forms.factory.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.ui.ObjectRenderer;
import org.riotfamily.common.xml.DocumentDigester;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.forms.ContainerElement;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.element.AbstractTextElement;
import org.riotfamily.forms.element.Calendar;
import org.riotfamily.forms.element.Checkbox;
import org.riotfamily.forms.element.ColorPicker;
import org.riotfamily.forms.element.EditableIfNew;
import org.riotfamily.forms.element.ElementGroup;
import org.riotfamily.forms.element.ImageCheckbox;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.element.NumberField;
import org.riotfamily.forms.element.PasswordField;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.element.Textarea;
import org.riotfamily.forms.element.TinyMCE;
import org.riotfamily.forms.element.collection.ListEditor;
import org.riotfamily.forms.element.collection.MapEditor;
import org.riotfamily.forms.element.select.CheckboxGroup;
import org.riotfamily.forms.element.select.ImageCheckboxGroup;
import org.riotfamily.forms.element.select.MultiSelectBox;
import org.riotfamily.forms.element.select.RadioButtonGroup;
import org.riotfamily.forms.element.select.SelectBox;
import org.riotfamily.forms.element.select.SelectElement;
import org.riotfamily.forms.element.suggest.AutocompleteTextField;
import org.riotfamily.forms.factory.ConfigurableElementFactory;
import org.riotfamily.forms.factory.ContainerElementFactory;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepositoryException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.validation.Validator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 * Strategy for parsing a DOM that follows the form-config schema.
 */
public class XmlFormRepositoryDigester implements DocumentDigester {

	public static final String NAMESPACE = "http://www.riotfamily.org/schema/forms/form-config";

	private Log log = LogFactory.getLog(XmlFormRepositoryDigester.class);

	private ConfigurableListableBeanFactory beanFactory;

	private XmlFormRepository formRepository;

	private HashMap elementClasses = new HashMap();

	private String formId;

	private ArrayList imports;

	private String currentPackage;

	private Resource resource;

	public XmlFormRepositoryDigester(XmlFormRepository formRepository,
			ConfigurableListableBeanFactory beanFactory) {

		this.formRepository = formRepository;
		this.beanFactory = beanFactory;

		elementClasses.put("group", ElementGroup.class);
		elementClasses.put("textfield", TextField.class);
		elementClasses.put("passwordfield", PasswordField.class);
		elementClasses.put("numberfield", NumberField.class);
		elementClasses.put("textarea", Textarea.class);
		elementClasses.put("richtext", TinyMCE.class);
		elementClasses.put("calendar", Calendar.class);
		elementClasses.put("color-picker", ColorPicker.class);
		elementClasses.put("checkbox", Checkbox.class);
		elementClasses.put("imagecheckbox", ImageCheckbox.class);
		elementClasses.put("nested-form", NestedForm.class);
		elementClasses.put("list", ListEditor.class);
		elementClasses.put("map", MapEditor.class);
		elementClasses.put("multi-selectbox", MultiSelectBox.class);
		elementClasses.put("selectbox", SelectBox.class);
		elementClasses.put("radio-group", RadioButtonGroup.class);
		elementClasses.put("checkbox-group", CheckboxGroup.class);
		elementClasses.put("imagecheckbox-group", ImageCheckboxGroup.class);
		elementClasses.put("editable-if-new", EditableIfNew.class);
		elementClasses.put("autocomplete", AutocompleteTextField.class);
	}

	public void digest(Document doc, Resource resource) {
		this.resource = resource;
		formId = null;
		imports = new ArrayList();
		currentPackage = null;

		Element root = doc.getDocumentElement();
		Iterator it = XmlUtils.getChildElements(root).iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String namespace = ele.getNamespaceURI();
			if (namespace == null || namespace.equals(NAMESPACE)) {
				if (DomUtils.nodeNameEquals(ele, "form")) {
					parseFormDefinition(ele);
				}
				else if (DomUtils.nodeNameEquals(ele, "package")) {
					parsePackageDefinition(ele);
				}
			}
		}

		it = imports.iterator();
		while (it.hasNext()) {
			((Import) it.next()).apply();
		}
	}

	protected void parseFormDefinition(Element formElement) {
		String beanClassName = XmlUtils.getAttribute(
				formElement, "bean-class");

		Class beanClass = getBeanClass(beanClassName);
		FormInitializer initializer = (FormInitializer) getOrCreate(
				formElement, "initializer", "initializer-class",
				FormInitializer.class);

		Validator validator = (Validator) getOrCreate(formElement,
				"validator", "validator-class", Validator.class);
				
		FormFactory formFactory = formRepository.createFormFactory(
				beanClass, initializer, validator);
		
		formId = formElement.getAttribute("id");
		Iterator it = XmlUtils.getChildElements(formElement).iterator();
		while (it.hasNext()) {
			parseElementDefinition((Element) it.next(), formFactory);
		}
		formRepository.registerFormFactory(formId, formFactory);
	}

	private Object getOrCreate(Element element, String refAttribute,
			String classNameAttribute, Class requiredClass) {

		String ref = XmlUtils.getAttribute(
				element, refAttribute);

		if (ref != null) {
			return beanFactory.getBean(ref, requiredClass);
		}
		else {
			String className = XmlUtils.getAttribute(
				element, classNameAttribute);

			if (className != null) {
				try {
					Class beanClass = ClassUtils.forName(className);
					Object obj = beanFactory.createBean(beanClass, 
							AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, 
							false);
					
					Assert.isInstanceOf(requiredClass, obj);
					return obj;
				}
				catch (ClassNotFoundException e) {
					throw new FatalBeanException(e.getMessage()); 
				}
			}
		}
		return null;
	}

	
	protected void parsePackageDefinition(Element ele) {
		currentPackage = XmlUtils.getAttribute(ele, "name");
		Iterator it = DomUtils.getChildElementsByTagName(
				ele, "form").iterator();

		while (it.hasNext()) {
			parseFormDefinition((Element) it.next());
		}
	}

	
	protected void parseElementDefinition(Element ele,
			ContainerElementFactory parentFactory) {

		if (DomUtils.nodeNameEquals(ele, "import")) {
			String formId = ele.getAttribute("form");
			imports.add(new Import(formId, this.formId, parentFactory));
		}
		else {
			ConfigurableElementFactory factory = createFactory(ele);
			parentFactory.addChildFactory(factory);
		}
	}

	
	protected ConfigurableElementFactory createFactory(Element ele) {
		Class elementClass = getElementClass(ele);
		ConfigurableElementFactory factory =
				new ConfigurableElementFactory(elementClass);

		factory.setBeanFactory(beanFactory);
		String beanClassName = XmlUtils.getAttribute(ele, "bean-class");
		if (beanClassName != null) {
			factory.setBeanClass(getBeanClass(beanClassName));
		}
		factory.setBind(XmlUtils.getAttribute(ele, "bind"));

		MutablePropertyValues pvs = getPropertyValues(ele);

		if (SelectElement.class.isAssignableFrom(elementClass)) {
			initSelectElement(ele, pvs);
		}
		else if (Checkbox.class.isAssignableFrom(elementClass)) {
			initCheckbox(ele, pvs);
		}
		else if (MapEditor.class.isAssignableFrom(elementClass)) {
			initMapEditor(ele, pvs);
		}
		else if (ListEditor.class.isAssignableFrom(elementClass)) {
			initListEditor(ele, pvs);
		}
		else if (AutocompleteTextField.class.isAssignableFrom(elementClass)) {
			initAutocompleteTextField(ele, pvs);
		}
		else if (TinyMCE.class.isAssignableFrom(elementClass)) {
			initTinyMCE(ele, pvs);
		}

		if (AbstractTextElement.class.isAssignableFrom(elementClass)) {
			renameProperty("default", "defaultText", pvs);
		}
		
		factory.setPropertyValues(pvs);

		if (ContainerElement.class.isAssignableFrom(elementClass)) {
			Iterator it = XmlUtils.getChildElements(ele).iterator();
			while (it.hasNext()) {
				parseElementDefinition((Element) it.next(), factory);
			}
		}

		return factory;
	}

	private void renameProperty(String name, String newName, MutablePropertyValues pvs) {
		PropertyValue pv = pvs.getPropertyValue(name);
		if (pv != null) {
			pvs.removePropertyValue(pv);
			pvs.addPropertyValue(newName, pv.getValue());
		}
	}

	private void initSelectElement(Element ele, MutablePropertyValues pvs) {
		Object model = getOptionsModel(ele);
		if (model != null) {
			log.debug("OptionsModel: " + model);
			pvs.addPropertyValue("options", model);
		}
	}
	
	private void initCheckbox(Element ele, MutablePropertyValues pvs) {
		pvs.removePropertyValue("default");
		if ("checked".equals(XmlUtils.getAttribute(ele, "default"))) {
			pvs.addPropertyValue("checkedByDefault", Boolean.TRUE);
		}
	}
	
	private void initListEditor(Element ele, MutablePropertyValues pvs) {
		Element itemElement = XmlUtils.getFirstChildElement(ele);
		ElementFactory itemFactory = createFactory(itemElement);
		pvs.addPropertyValue("itemElementFactory", itemFactory);
	}
	
	private void initMapEditor(Element ele, MutablePropertyValues pvs) {
		ObjectRenderer renderer = (ObjectRenderer) getOrCreate(
				ele, "label-renderer", "label-renderer-class",
				ObjectRenderer.class);
		
		if (renderer != null) {
			pvs.removePropertyValue("labelRenderer");
			pvs.removePropertyValue("labelRendererClass");
			pvs.addPropertyValue("labelRenderer", renderer);
		}
		Element keyElement = XmlUtils.getFirstChildElement(ele);
		if (DomUtils.nodeNameEquals(keyElement, "key-element")) {
			ElementFactory factory = createFactory(XmlUtils.getFirstChildElement(keyElement));
			pvs.addPropertyValue("keyElementFactory", factory);
		}
		else {
			pvs.addPropertyValue("keyOptionsModel", getOptionsModel(ele));
		}
		Element itemElement = XmlUtils.getNextSiblingElement(keyElement);
		ElementFactory itemFactory = createFactory(itemElement);
		pvs.addPropertyValue("itemElementFactory", itemFactory);
	}
	
	private void initAutocompleteTextField(Element ele, MutablePropertyValues pvs) {
		pvs.addPropertyValue("model", beanFactory.getBean(
				XmlUtils.getAttribute(ele, "model")));
	}
	
	private void initTinyMCE(Element ele, MutablePropertyValues pvs) {
		pvs.addPropertyValue("config", formRepository.getTinyMCEConfig(
				XmlUtils.getAttribute(ele, "config")));
	}

	protected Class getElementClass(Element ele) {
		String type = null;
		if (DomUtils.nodeNameEquals(ele, "element")) {
			type = XmlUtils.getAttribute(ele, "type");
		}
		else {
			String namespace = ele.getNamespaceURI();
			if (namespace == null || namespace.equals(NAMESPACE)) {
				type = XmlUtils.getLocalName(ele);
			}
			else {
				type = '{' + namespace + '}' + ele.getLocalName();
			}
		}
		return getElementClass(type);
	}

	protected Class getElementClass(String type) {
		Class elementClass = (Class) elementClasses.get(type);
		if (elementClass == null) {
			elementClass = formRepository.getElementClass(type);
		}
		if (elementClass == null) {
			throw new FormRepositoryException(resource, formId,
					"Unknown element type: " + type);
		}
		return elementClass;
	}

	protected Class getBeanClass(String beanClassName) {
		if (beanClassName == null) {
			return formRepository.getDefaultBeanClass();
		}
		if (beanClassName.indexOf('.') == -1 && currentPackage != null) {
			beanClassName = currentPackage + '.' + beanClassName;
		}
		try {
			return Class.forName(beanClassName);
		}
		catch (ClassNotFoundException e) {
			throw new FormRepositoryException(resource, formId,
					"beanClass not found", e);
		}
	}

	protected MutablePropertyValues getPropertyValues(Element element) {
		MutablePropertyValues pvs = new MutablePropertyValues();

		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; attrs != null && i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);
			String name = attr.getName();
			if (!"bind".equals(name)
					&& !"type".equals(name)
					&& !"bean-class".equals(name)) {

				String property = FormatUtils.xmlToCamelCase(attr.getName());
				log.debug("Setting property " + property + " to " + attr.getValue());
				pvs.addPropertyValue(property, attr.getValue());
			}
		}

		Iterator it = XmlUtils.getChildElementsByRegex(
				element, "property|set-property").iterator();

		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String name = XmlUtils.getAttribute(ele, "name");

			Object value = null;

			String beanName = XmlUtils.getAttribute(ele, "ref");
			if (beanName != null) {
				value = beanFactory.getBean(beanName);
			}
			else {
				value = XmlUtils.getAttribute(ele, "value");
			}

			pvs.addPropertyValue(name, value);
		}

		return pvs;
	}

	private Object getOptionsModel(Element ele) {
		log.debug("Looking for OptionsModel ...");
		Element modelElement = XmlUtils.getFirstChildByRegex(ele, "model|options|key-options");
		if (modelElement != null) {
			Object model = null;
			String className = XmlUtils.getAttribute(modelElement, "class");
			if (className != null) {
				try {
					model = PropertyUtils.newInstance(className);
				}
				catch (BeansException e) {
					throw new FormRepositoryException(resource, formId,
							"Error creating OptionsModel", e);
				}
			}
			else {
				String beanName = XmlUtils.getAttribute(modelElement, "ref");
				if (beanName != null) {
					model = beanFactory.getBean(beanName);
				}
			}
			if (model != null) {
				List propertyElements = XmlUtils.getChildElementsByRegex(
						modelElement, "property|set-property");

				XmlUtils.populate(model, propertyElements, beanFactory);
			}
			return model;
		}
		else {
			ArrayList values = new ArrayList();
			Iterator it = DomUtils.getChildElementsByTagName(ele, "option").iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String beanName = XmlUtils.getAttribute(e, "ref");
				if (beanName != null) {
					values.add(beanFactory.getBean(beanName));
				}
				else {
					values.add(XmlUtils.getAttribute(e, "value"));
				}
			}
			return values;
		}
	}
	
	

	private class Import {

		private ContainerElementFactory parent;

		private int insertAt;
		
		private String formId;

		private String importedFormId;

		Import(String importedFormId, String formId, ContainerElementFactory parent) {
			this.importedFormId = importedFormId;
			this.formId = formId;
			this.parent = parent;
			insertAt = parent.getChildFactories().size();
		}

		public void apply() {
			FormFactory formFactory = formRepository.getFormFactory(importedFormId);
			parent.getChildFactories().addAll(insertAt,
					formFactory.getChildFactories());
			formRepository.registerImport(formId, formFactory);
		}

	}

}
