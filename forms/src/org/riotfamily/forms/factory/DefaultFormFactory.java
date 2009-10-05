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

import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;


/**
 * Form factory used by
 * {@link org.riotfamily.forms.factory.xml.XmlFormRepositoryDigester}.
 * Since there are no dependencies to the xml package this class
 * could also be useful for other custom implementations.
 */
public class DefaultFormFactory implements FormFactory {

	/** Class to be edited by the form */
	private Class<?> beanClass;

	private Class<? extends EditorBinder> editorBinderClass;
	
	/** List of factories to create child elements */
	private List<ElementFactory> childFactories = new LinkedList<ElementFactory>();

	private FormInitializer initializer;

	private Validator validator;


	public DefaultFormFactory(FormInitializer initializer, 
			Validator validator) {
		
		this.initializer = initializer;
		this.validator = validator;
	}
	
	public DefaultFormFactory(FormInitializer initializer, 
			Validator validator, Class<?> beanClass) {
		
		this.initializer = initializer;
		this.validator = validator;
		this.beanClass = beanClass;
	}
	
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	
	public void setEditorBinderClass(Class<? extends EditorBinder> editorBinderClass) {
		this.editorBinderClass = editorBinderClass;
	}
	
	public Class<?> getBeanClass() {
		return this.beanClass;
	}

	/**
	 * Adds an ElementFactory to the list of child factories.
	 */
	public void addChildFactory(ElementFactory factory) {
		childFactories.add(factory);
	}

	public List<ElementFactory> getChildFactories() {
		return this.childFactories;
	}

	public Form createForm() {
		Form form = new Form();
		if (editorBinderClass != null) {
			try {
				form.setEditorBinder(editorBinderClass.newInstance());
			} 
			catch (InstantiationException e) {
				throw new BeanCreationException("Error creating EditorBinder", e);
			}
			catch (IllegalAccessException e) {
				throw new BeanCreationException("Error creating EditorBinder", e);
			}
		}
		else {
			Assert.notNull(beanClass, "Either a beanClass or a editorBinderClass must be set");
			form.setBeanClass(beanClass);			
		}
		form.setInitializer(initializer);
		form.setValidator(validator);
		for (ElementFactory factory : childFactories) { 
			Element child = factory.createElement(null, form, true);
			form.addElement(child);
		}
		return form;
	}

}
