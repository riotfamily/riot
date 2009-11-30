/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.forms.factory;

import java.util.LinkedList;
import java.util.List;

import org.riotfamily.common.beans.property.IntArrayPropertyEditor;
import org.riotfamily.forms.BeanEditor;
import org.riotfamily.forms.ContainerElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;


/**
 * Configurable element factory that can be used to create arbitary 
 * form elements. This class is used by 
 * {@link org.riotfamily.forms.factory.xml.XmlFormRepositoryDigester}
 * but could also be useful for custom implementations. 
 */
public class ConfigurableElementFactory implements ContainerElementFactory, 
		EditorElementFactory {

	private Logger log = LoggerFactory.getLogger(ConfigurableElementFactory.class);
	
	/** The class to create */
	private Class<? extends Element> elementClass;
	
	/** Property name the element should be bound to */
	private String bind;
	
	/** Class to be edited by a BeanEditor */
	private Class<?> beanClass;
	
	/** Properties to be set after element creation */
	private PropertyValues propertyValues;
	
	/** List of factories to create optional child elements */
	private List<ElementFactory> childFactories = new LinkedList<ElementFactory>();
	
	/** BeanFactory used to lookup bean references */
	private ConfigurableListableBeanFactory beanFactory;
	
	/**
	 * Creates a new factory for the given element class.
	 */
	public ConfigurableElementFactory(Class<? extends Element> elementClass) {
		this.elementClass = elementClass;
	}
	
	public Class<? extends Element> getElementClass() {
		return this.elementClass;
	}

	/**
	 * Sets the BeanFactory that is used to lookup bean references.
	 */
	public void setBeanFactory(ConfigurableListableBeanFactory beanFactory) {
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
	public void setBeanClass(Class<?> beanClass) {
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
	public Class<?> getBeanClass() {
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
	public void setChildFactories(List<ElementFactory> childFactories) {
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
	public List<ElementFactory> getChildFactories() {
		return childFactories;
	}
	
	/**
	 * Returns a new instance of the configured element class.
	 * @see ElementFactory#createElement(Element, Form, boolean)
	 */
	public Element createElement(Element parent, Form form, boolean bind) {
		log.debug("Creating element " + elementClass);
		
		Element element = (Element) beanFactory.createBean(elementClass, 
				AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
		
		element.setParent(parent);
		if (beanClass != null) {
			BeanEditor bee = (BeanEditor) element;
			bee.setBeanClass(beanClass);
		}
		
		populateElement(element);
		
		if (bind && element instanceof Editor) {
			if (this.bind != null) {
				BeanEditor beanEditor = findEditor(parent, form);
				Editor editor = (Editor) element;
				beanEditor.bind(editor, this.bind);
			}
		}
		
		if (element instanceof ContainerElement) {
			createChildElements((ContainerElement) element, form);
		}
		
		beanFactory.initializeBean(element, null);
		
		return element;
	}
	
	/**
	 * Called by {@link #createElement(Element, Form)} to populate the given 
	 * element with the property values set via 
	 * {@link #setPropertyValues(PropertyValues)}. Values will be resolved if 
	 * necessary by calling {@link #resolveValueIfNecessary(Object)}.
	 */
	protected void populateElement(Element element) {
		if (propertyValues == null) {
			return;
		}
		BeanWrapper beanWrapper = new BeanWrapperImpl(element);
		beanWrapper.registerCustomEditor(int[].class, new IntArrayPropertyEditor());
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
			for (ElementFactory factory : childFactories) {
				Element child = factory.createElement(parent, form, true);
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
