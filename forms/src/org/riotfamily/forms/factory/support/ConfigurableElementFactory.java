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
package org.riotfamily.forms.factory.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.bind.BeanEditor;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.element.ContainerElement;
import org.riotfamily.forms.factory.ContainerElementFactory;
import org.riotfamily.forms.factory.EditorElementFactory;
import org.riotfamily.forms.factory.ElementFactory;
import org.riotfamily.forms.factory.FormDefinitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.RuntimeBeanReference;


/**
 * Configurable element factory that can be used to create arbitary 
 * form elements. This class is used by 
 * {@link org.riotfamily.forms.factory.xml.XmlFormRepositoryDigester}
 * but could also be useful for custom implementations. 
 */
public class ConfigurableElementFactory implements ContainerElementFactory, 
		EditorElementFactory {

	private Log log = LogFactory.getLog(ConfigurableElementFactory.class);
	
	/** The class to create */
	private Class elementClass;
	
	/** Property name the element should be bound to */
	private String bind;
	
	/** Class to be edited by a BeanEditor */
	private Class beanClass;
	
	/** Properties to be set after element creation */
	private PropertyValues propertyValues;
	
	/** List of factories to create optional child elements */
	private List childFactories = new LinkedList();
	
	/** BeanFactory used to lookup bean references */
	private BeanFactory beanFactory;
	
	/**
	 * Creates a new factory for the given element class.
	 */
	public ConfigurableElementFactory(Class elementClass) {
		this.elementClass = elementClass;
	}
	
	public Class getElementClass() {
		return this.elementClass;
	}

	/**
	 * Sets the BeanFactory that is used to lookup bean references.
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Sets the PropertyValues that will be set on the element after creation.
	 */
	public void setPropertyValues(PropertyValues propertyValues) {
		this.propertyValues = propertyValues;
	}

	/**
	 * If the factory is set up to create a {@link BeanEditor}, the
	 * type of the bean that is to be edited can be set.
	 * 
	 * @throws FormDefinitionException if the element does not implement
	 * 		{@link BeanEditor}
	 */
	public void setBeanClass(Class beanClass) {
		if (beanClass != null && !BeanEditor.class
				.isAssignableFrom(elementClass)) {
			
			throw new FormDefinitionException("Element class must implement "
					+ BeanEditor.class.getName());
		}
		this.beanClass = beanClass;
	}
	
	/**
	 * @return Returns the beanClass.
	 */
	public Class getBeanClass() {
		return beanClass;
	}

	/**
	 * Sets the name of the property that elements created by this factory
	 * shall be bound to. 
	 * 
	 * @throws FormDefinitionException if the element does not implement
	 * 		{@link Editor}
	 */
	public void setBind(String bind) {
		if (bind != null && !Editor.class.isAssignableFrom(elementClass)) {
			throw new FormDefinitionException("Element class must implement "
					+ Editor.class.getName());
		}
		this.bind = bind;
	}
	
	/**
	 * Returns the name of the property that elements created by this factory
	 * shall be bound to or <code>null</code> if no binding should be 
	 * performed.
	 *  
	 * @see EditorElementFactory#getBind()
	 */
	public String getBind() {
		return bind;
	}
	
	/**
	 * Sets a list of element factories that will be used to create child 
	 * elements which will be added to the elements beeing created by this 
	 * factory.
	 * 
	 * @throws FormDefinitionException if the element does not implement
	 * 		{@link ContainerElement}
	 */
	public void setChildFactories(List childFactories) {
		if (childFactories != null && !ContainerElement.class
				.isAssignableFrom(elementClass)) {
			
			throw new FormDefinitionException("Element class must implement "
					+ ContainerElement.class.getName());
		}
		this.childFactories = childFactories;
	}
	
	/**
	 * Adds an ElementFactory to the list of child factories.
	 * @see #setChildFactories(List)
	 */
	public void addChildFactory(ElementFactory factory) {
		if (!ContainerElement.class.isAssignableFrom(elementClass)) {
			throw new FormDefinitionException("Element class must implement "
					+ ContainerElement.class.getName());
		}
		childFactories.add(factory);
	}
	
	/**
	 * Returns a list of element factories used to create nested elements.
	 * @see #setChildFactories(List)
	 */
	public List getChildFactories() {
		return childFactories;
	}
	
	/**
	 * Returns a new instance of the configured element class.
	 * @see ElementFactory#createElement(Element, Form)
	 */
	public Element createElement(Element parent, Form form) {
		log.debug("Creating element " + elementClass);
		Element element = (Element) BeanUtils.instantiateClass(elementClass);
		element.setParent(parent);
		if (beanClass != null) {
			BeanEditor bee = (BeanEditor) element;
			bee.setBeanClass(beanClass);
		}
		if (element instanceof BeanFactoryAware) {
			BeanFactoryAware bfa = (BeanFactoryAware) element;
			bfa.setBeanFactory(beanFactory);
		}
		populateElement(element);
		
		if (element instanceof Editor) {
			if (bind != null) {
				log.debug("Bind: " + bind);
				BeanEditor beanEditor = findEditor(parent, form);
				Editor editor = (Editor) element;
				beanEditor.bind(editor, bind);
			}
		}
		
		if (element instanceof ContainerElement) {
			createChildElements((ContainerElement) element, form);
		}
				
		return element;
	}
	
	/**
	 * Called by {@link #createElement()} to populate the given element with 
	 * the poperty values set via {@link #setPropertyValues(PropertyValues)}. 
	 * Values will be resolved if necessary by calling 
	 * {@link #resolveValueIfNecessary(Object)}.
	 * 
	 * @see #setPropertyValues(PropertyValues)
	 */
	protected void populateElement(Element element) {
		if (propertyValues == null) {
			return;
		}
		BeanWrapper beanWrapper = new BeanWrapperImpl(element);
		MutablePropertyValues pvs = new MutablePropertyValues();
		PropertyValue[] pvArray = propertyValues.getPropertyValues();
		for (int i = 0; i < pvArray.length; i++) {
			PropertyValue pv = pvArray[i];
			Object resolvedValue = resolveValueIfNecessary(pv.getValue());
			pvs.addPropertyValue(pvArray[i].getName(), resolvedValue);
		}
		beanWrapper.setPropertyValues(pvs);
	}
	
	/**
	 * Called by {@link #populateElement(Element)} to support runtime 
	 * references to prototype beans.
	 * 
	 * @see RuntimeBeanReference
	 */
	protected Object resolveValueIfNecessary(Object value) {
		if (value instanceof RuntimeBeanReference) {
			RuntimeBeanReference ref = (RuntimeBeanReference) value;
			return beanFactory.getBean(ref.getBeanName());
		}
		return value;
	}
	
	protected void createChildElements(ContainerElement parent, Form form) {
		if (childFactories != null) {
			Iterator it = childFactories.iterator();
			while (it.hasNext()) {
				ElementFactory factory = (ElementFactory) it.next();
				Element child = factory.createElement(parent, form);
				parent.addElement(child);
			}
		}
	}
	
	protected BeanEditor findEditor(Element parent, Form form) {
		while (parent != null) {
			log.debug("Checking " + parent);
			if (parent instanceof BeanEditor) {
				log.debug("Found.");
				return (BeanEditor) parent;
			}
			parent = parent.getParent();
		}
		return form;
	}
			
}
