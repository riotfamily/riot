package org.riotfamily.forms.factory.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.factory.ElementFactory;
import org.riotfamily.forms.factory.FormFactory;


/**
 * Form factory used by 
 * {@link org.riotfamily.forms.factory.xml.XmlFormRepositoryDigester}.
 * Since there are no dependencies to the xml package this class
 * could also be useful for other custom implementations.
 */
public class DefaultFormFactory implements FormFactory {
	
	/** Class to be edited by the form */
	private Class beanClass;
	
	/** List of factories to create child elements */
	private List childFactories = new LinkedList();
	
	private FormInitializer initializer;
	
	public Class getBeanClass() {
		return this.beanClass;
	}

	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}
	
	/**
	 * Adds an ElementFactory to the list of child factories.
	 */
	public void addChildFactory(ElementFactory factory) {
		childFactories.add(factory);
	}

	public List getChildFactories() {
		return this.childFactories;
	}

	public void setInitializer(FormInitializer initializer) {
		this.initializer = initializer;
	}

	public Form createForm() {
		Form form = new Form();
		form.setBeanClass(beanClass);
		form.setInitializer(initializer);
		Iterator it = childFactories.iterator();
		while (it.hasNext()) {
			ElementFactory factory = (ElementFactory) it.next();
			Element child = factory.createElement(null, form);
			form.addElement(child);
		}
		return form;
	}
	
}
