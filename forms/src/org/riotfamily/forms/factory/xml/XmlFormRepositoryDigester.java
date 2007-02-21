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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.web.file.FileStore;
import org.riotfamily.common.xml.DocumentDigester;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.element.ContainerElement;
import org.riotfamily.forms.element.SelectElement;
import org.riotfamily.forms.element.core.Calendar;
import org.riotfamily.forms.element.core.Checkbox;
import org.riotfamily.forms.element.core.CheckboxGroup;
import org.riotfamily.forms.element.core.ElementGroup;
import org.riotfamily.forms.element.core.FileUpload;
import org.riotfamily.forms.element.core.ImageCheckbox;
import org.riotfamily.forms.element.core.ImageUpload;
import org.riotfamily.forms.element.core.ListEditor;
import org.riotfamily.forms.element.core.MapEditor;
import org.riotfamily.forms.element.core.NestedForm;
import org.riotfamily.forms.element.core.NumberField;
import org.riotfamily.forms.element.core.PasswordField;
import org.riotfamily.forms.element.core.RadioButtonGroup;
import org.riotfamily.forms.element.core.SelectBox;
import org.riotfamily.forms.element.core.TextField;
import org.riotfamily.forms.element.core.Textarea;
import org.riotfamily.forms.element.core.TinyMCE;
import org.riotfamily.forms.element.dom.XmlElement;
import org.riotfamily.forms.element.dom.XmlSequence;
import org.riotfamily.forms.element.support.EditableIfNew;
import org.riotfamily.forms.element.support.select.OptionsModel;
import org.riotfamily.forms.element.support.select.StaticOptionsModel;
import org.riotfamily.forms.factory.ContainerElementFactory;
import org.riotfamily.forms.factory.ElementFactory;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepositoryException;
import org.riotfamily.forms.factory.support.ConfigurableElementFactory;
import org.riotfamily.forms.factory.support.DefaultFormFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 * Strategy for parsing a DOM that follows the form-config schema.
 */
public class XmlFormRepositoryDigester implements DocumentDigester {

	public static final String NAMESPACE = "http://www.riotfamily.org/schema/forms/form-config";
	
	public static final String DEFAULT = "default";
	
	public static final String FORM = "form";
	
	public static final String FORM_ID = "id";
	
	public static final String FORM_BEAN_CLASS = "bean-class";
	
	public static final String FORM_INITIALIZER = "initializer";
	
	public static final String FORM_INITIALIZER_CLASS = "initializer-class";
	
	public static final String PACKAGE = "package";
	
	public static final String PACKAGE_NAME = "name";
	
	public static final String CHECKBOX_CHECKED = "checked";
	
	public static final String GENERIC_ELEMENT = "element";
	
	public static final String GENERIC_ELEMENT_TYPE = "type";
	
	public static final String ELEMENT_BIND = "bind";
	
	public static final String ELEMENT_BEAN_CLASS = "bean-class";
	
	public static final String FILE_STORE = "store";
	
	public static final String PROPERTY = "set-property";
	
	public static final String PROPERTY_NAME = "name";
	
	public static final String PROPERTY_VALUE = "value";
	
	public static final String PROPERTY_BEAN_REF = "ref";
	
	public static final String MODEL = "model";
	
	public static final String MODEL_CLASS = "class";
	
	public static final String MODEL_BEAN_REF = "ref";
	
	public static final String IMPORT = "import";
	
	public static final String IMPORT_FORM_REF = "form";

	private Log log = LogFactory.getLog(XmlFormRepositoryDigester.class);
	
	private BeanFactory beanFactory;
	
	private XmlFormRepository formRepository;
	
	private HashMap elementClasses = new HashMap();
	
	private DefaultFormFactory formFactory;
	
	private String formId;
	
	private ArrayList imports;
	
	private String currentPackage;
	
	private Resource resource;
	
	public XmlFormRepositoryDigester(XmlFormRepository formRepository, 
			BeanFactory beanFactory) {
		
		this.formRepository = formRepository;
		this.beanFactory = beanFactory;
		
		elementClasses.put("group", ElementGroup.class);
		elementClasses.put("textfield", TextField.class);
		elementClasses.put("passwordfield", PasswordField.class);
		elementClasses.put("numberfield", NumberField.class);
		elementClasses.put("textarea", Textarea.class);
		elementClasses.put("richtext", TinyMCE.class);
		elementClasses.put("calendar", Calendar.class);
		elementClasses.put("checkbox", Checkbox.class);
		elementClasses.put("imagecheckbox", ImageCheckbox.class);
		elementClasses.put("nested-form", NestedForm.class);
		elementClasses.put("list", ListEditor.class);
		elementClasses.put("map", MapEditor.class);
		elementClasses.put("selectbox", SelectBox.class);
		elementClasses.put("radio-group", RadioButtonGroup.class);
		elementClasses.put("checkbox-group", CheckboxGroup.class);
		elementClasses.put("file-upload", FileUpload.class);
		elementClasses.put("image-upload", ImageUpload.class);
		elementClasses.put("editable-if-new", EditableIfNew.class);
		elementClasses.put("xml-element", XmlElement.class);
		elementClasses.put("xml-sequence", XmlSequence.class);
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
				if (DomUtils.nodeNameEquals(ele, FORM)) {
					parseFormDefinition(ele);
				}
				else if (DomUtils.nodeNameEquals(ele, PACKAGE)) {
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
		formFactory = new DefaultFormFactory();
		formId = formElement.getAttribute(FORM_ID);
		
		String beanClassName = XmlUtils.getAttribute(
				formElement, FORM_BEAN_CLASS);
				
		formFactory.setBeanClass(getBeanClass(beanClassName));
		
		String initializerName = XmlUtils.getAttribute(
				formElement, FORM_INITIALIZER);
		
		if (initializerName != null) {
			formFactory.setInitializer((FormInitializer) beanFactory.getBean(
					initializerName, FormInitializer.class));
		}
		else {
			String initializerClass = XmlUtils.getAttribute(
				formElement, FORM_INITIALIZER_CLASS);
			
			if (initializerClass != null) {
				formFactory.setInitializer((FormInitializer) 
						PropertyUtils.newInstance(initializerClass));
			}	
		}
		
		Iterator it = XmlUtils.getChildElements(formElement).iterator();
		while (it.hasNext()) {
			parseElementDefinition((Element) it.next(), formFactory);
		}
		formRepository.registerFormFactory(formId, formFactory);
	}
	
	protected void parsePackageDefinition(Element ele) {
		currentPackage = XmlUtils.getAttribute(ele, PACKAGE_NAME);
		Iterator it = DomUtils.getChildElementsByTagName(
				ele, FORM).iterator();
		
		while (it.hasNext()) {
			parseFormDefinition((Element) it.next());
		}
	}
	
	protected void parseElementDefinition(Element ele, 
			ContainerElementFactory parentFactory) {
		
		if (DomUtils.nodeNameEquals(ele, IMPORT)) {
			String formId = ele.getAttribute(IMPORT_FORM_REF);
			imports.add(new Import(formId, parentFactory));
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
		String beanClassName = XmlUtils.getAttribute(ele, ELEMENT_BEAN_CLASS);
		if (beanClassName != null) {
			factory.setBeanClass(getBeanClass(beanClassName));
		}
		factory.setBind(XmlUtils.getAttribute(ele, ELEMENT_BIND));

		MutablePropertyValues pvs = getPropertyValues(ele);
		
		if (SelectElement.class.isAssignableFrom(elementClass)) {
			log.debug("Looking for OptionsModel ...");
			OptionsModel model = getOptionsModel(ele);
			if (model != null) {
				log.debug("OptionsModel: " + model);
				pvs.addPropertyValue("optionsModel", model);
			}
		}
		
		if (Checkbox.class.isAssignableFrom(elementClass)) {
			pvs.removePropertyValue(DEFAULT);
			if (CHECKBOX_CHECKED.equals(XmlUtils.getAttribute(ele, DEFAULT))) {
				pvs.addPropertyValue("checkedByDefault", Boolean.TRUE);
			}
		}
		
		if (FileUpload.class.isAssignableFrom(elementClass)) {
			pvs.removePropertyValue(FILE_STORE);
			String ref = XmlUtils.getAttribute(ele, FILE_STORE);
			if (ref != null) {
				FileStore fileStore = (FileStore) beanFactory.getBean(
						ref, FileStore.class);
				
				pvs.addPropertyValue("fileStore", fileStore);
			}
			
			if (formRepository.getMimetypesMap() != null) {
				pvs.addPropertyValue("mimetypesMap", 
						formRepository.getMimetypesMap()); 
			}
		}
		
		if (ImageUpload.class.isAssignableFrom(elementClass)) {
			pvs.addPropertyValue("cropper", formRepository.getImageCropper()); 
		}
		
		//TODO We should consider adding a common interface ...
		if (ListEditor.class.isAssignableFrom(elementClass)
				|| MapEditor.class.isAssignableFrom(elementClass)) {
			
			Element itemElement = XmlUtils.getFirstChildElement(ele);
			ElementFactory itemFactory = createFactory(itemElement);
			pvs.addPropertyValue("itemElementFactory", itemFactory);
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
	
	protected Class getElementClass(Element ele) {
		String type = null;
		if (DomUtils.nodeNameEquals(ele, GENERIC_ELEMENT)) {
			type = XmlUtils.getAttribute(ele, GENERIC_ELEMENT_TYPE);
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
			if (!ELEMENT_BIND.equals(name)
					&& !GENERIC_ELEMENT_TYPE.equals(name)
					&& !FORM_BEAN_CLASS.equals(name)) {
				
				String property = FormatUtils.xmlToCamelCase(attr.getName());
				log.debug("Setting property " + property + " to " + attr.getValue());
				pvs.addPropertyValue(property, attr.getValue());
			}
		}
		
		Iterator it = DomUtils.getChildElementsByTagName(
				element, PROPERTY).iterator();
		
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String name = XmlUtils.getAttribute(ele, PROPERTY_NAME);
			
			Object value = null;
			
			String beanName = XmlUtils.getAttribute(ele, PROPERTY_BEAN_REF);
			if (beanName != null) {
				value = beanFactory.getBean(beanName);
			}
			else {
				value = XmlUtils.getAttribute(ele, PROPERTY_VALUE);
			}
			
			pvs.addPropertyValue(name, value);
		}

		return pvs;
	}
		
	protected OptionsModel getOptionsModel(Element element) {
		Element ele = DomUtils.getChildElementByTagName(element, MODEL);
		OptionsModel model = null;
		if (ele != null) {
			String className = XmlUtils.getAttribute(ele, MODEL_CLASS);
			if (className != null) {
				try {
					model = (OptionsModel) PropertyUtils.newInstance(className);
				}
				catch (BeansException e) {
					throw new FormRepositoryException(resource, formId, 
							"Error creating OptionsModel", e);
				}
			}
			else {
				String beanName = XmlUtils.getAttribute(
						ele, MODEL_BEAN_REF);
				
				if (beanName != null) {
					Object bean = beanFactory.getBean(beanName);
					if (bean instanceof OptionsModel) {
						model = (OptionsModel) bean;
					}
					else if (bean instanceof Collection) {
						model = new StaticOptionsModel((Collection) bean);
					}
					else {
						throw new FormRepositoryException(resource, formId, 
								"Bean [" + beanName + "] is neither an " +
								"OptionsModel nor a Collection: " + bean);
					}
				}
			}
			if (model != null) {
				List setPropElements = DomUtils.getChildElementsByTagName(
						ele, PROPERTY);
				
				XmlUtils.populate(model, setPropElements, beanFactory);
			}
		}
		return model;
	}
	
	private class Import {
		
		private ContainerElementFactory parent;
		
		private int insertAt;
		
		private String formId;
		
		Import(String formId, ContainerElementFactory parent) {
			this.formId = formId;
			this.parent = parent;
			insertAt = parent.getChildFactories().size();
		}
		
		public void apply() {
			FormFactory formFactory = formRepository.getFormFactory(formId);
			parent.getChildFactories().addAll(insertAt, 
					formFactory.getChildFactories());
		}

	}
	
}
