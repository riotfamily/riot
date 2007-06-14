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

import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.factory.ElementFactory;
import org.riotfamily.forms.factory.FormFactory;
import org.springframework.validation.Validator;


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

	private Validator validator;

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

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public Form createForm() {
		Form form = new Form();
		form.setBeanClass(beanClass);
		form.setInitializer(initializer);
		form.setValidator(validator);
		Iterator it = childFactories.iterator();
		while (it.hasNext()) {
			ElementFactory factory = (ElementFactory) it.next();
			Element child = factory.createElement(null, form);
			form.addElement(child);
		}
		return form;
	}

}
