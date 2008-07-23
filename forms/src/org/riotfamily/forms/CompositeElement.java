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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.request.FormRequest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;


/**
 * Abstract superclass for elements that consist of several child elements.  
 * Calls to <code>processRequest()</code> and <code>render()</code> are 
 * automatically delegated to the components. Addionally the components are
 * initialized, i.e. <code>setParent()</code>, <code>setForm()</code> and 
 * <code>Form.registerElement()</code> are called.  
 */
public abstract class CompositeElement extends AbstractEditorBase 
		implements BeanFactoryAware {

	private List components = new ArrayList();
	
	private boolean surroundByDiv;
	
	private AutowireCapableBeanFactory beanFactory;
	
	/**
	 * Empty default constructor.
	 */
	public CompositeElement() {
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof AutowireCapableBeanFactory) {
			this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
		}
	}
	
	protected List getComponents() {
		return components;
	}

	/**
	 * Adds the given element to the list of components. If a form as already
	 * been set {@link #initComponent(Element)} is 
	 * invoked, otherwise initialization is deferred until 
	 * {@link #setForm(Form)} is called.
	 * 
	 * @param element the element to add
	 */
	protected void addComponent(Element element) {
		components.add(element);
		element.setParent(this);
		if (getForm() != null) {
			initComponent(element);
		}
	}

	/**
	 * Removes the given component. Note that the element is
	 * not unregistered from the form.
	 */
	protected void removeComponent(Element element) {
		components.remove(element);
	}

	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	/**
	 * Invokes <code>initComponent(Element)</code> on all components and 
	 * finally calls <code>initCompositeElement()</code>.
	 */
	protected final void afterFormSet() {
		Iterator it = components.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			initComponent(element);
		}
		initCompositeElement();
	}
	
	/**
	 * Subclasses may override this method to perform initialization tasks.
	 * A reference to the form will be set at this point and all components
	 * will be initialized.
	 * 
	 *The default implementation does nothing.
	 */
	protected void initCompositeElement() {
	}

	/**
	 * Calls <code>processRequestInternal()</code> and afterwards 
	 * <code>processRequestComponents()</code> to process the components.
	 */
	public void processRequest(FormRequest request) {
		processRequestInternal(request);		
		processRequestCompontents(request);
	}
		
	/**
	 * Processes the request for all the components
	 */
	protected void processRequestCompontents(FormRequest request) {
		// Temporary list to allow concurrent modification
		List tempList = new ArrayList(components);
		Iterator it = tempList.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.isEnabled()) {
				log.debug("Processing component: " + element);
				element.processRequest(request);
			}
		}
	}
	
	/**
	 * Sets a reference to the form and registers the element by calling 
	 * {@link Form#registerElement(Element)}.
	 *  
	 * @throws IllegalStateException if form is null
	 */
	protected void initComponent(Element element) {
		Assert.state(getForm() != null, "The form must be set");
		if (beanFactory != null) {
			beanFactory.initializeBean(element, null);
		}
		getForm().registerElement(element);
	}

	/**
	 * Called before processRequest() is invoked on the contained elements.
	 * Subclasses can override this method to perform custom processing. The
	 * default implementation does nothing.
	 */
	protected void processRequestInternal(FormRequest request) {
	}

	protected boolean isSurroundByDiv() {
		return this.surroundByDiv;
	}

	protected void setSurroundByDiv(boolean surroundByDiv) {
		this.surroundByDiv = surroundByDiv;
	}

	protected void renderInternal(PrintWriter writer) {		
		if (surroundByDiv) {
			TagWriter divTag = new TagWriter(writer);
			divTag.start(Html.DIV).attribute(Html.COMMON_ID, getId()).body();
			renderComponents(writer);
			divTag.end();
		}
		else {
			renderComponents(writer);
		}
	}
	
	protected void renderComponents(PrintWriter writer) {
		Iterator it = components.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			log.debug("Rendering component " + element);
			element.render(writer);
		}
	}
	
	/**
	 * Delegates the call to the first component.
	 */
	public void focus() {
		if (!components.isEmpty()) {
			((Element) components.get(0)).focus();
		}
	}
	
	/**
	 * Helper method to check for composite elements in templates.
	 * Always returns <code>true</code>
	 */
	public boolean isCompositeElement() {
		return true;
	}
}