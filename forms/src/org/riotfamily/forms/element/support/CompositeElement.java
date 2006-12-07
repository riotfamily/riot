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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.support;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;


/**
 * Abstract superclass for elements that consist of several child elements.  
 * Calls to <code>processRequest()</code> and <code>render()</code> are 
 * automatically delegated to the components. Addionally the components are
 * initialized, i.e. <code>setParent()</code>, <code>setForm()</code> and 
 * <code>Form.registerElement()</code> are called.  
 */
public abstract class CompositeElement extends AbstractEditorBase {

	private List components = new ArrayList();
	
	private boolean surroundBySpan;
	
	/**
	 * Empty default constructor.
	 */
	public CompositeElement() {
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
	public void processRequest(HttpServletRequest request) {
		processRequestInternal(request);		
		processRequestCompontents(request);
		revalidate();
	}

	protected final void revalidate() {
		getForm().getErrors().removeErrors(this);
		validate();
	}
	
	protected void validate() {		
	}
	
	/**
	 * Processes the request for all the components
	 */
	protected final void processRequestCompontents(HttpServletRequest request) {
		// Temporary list to allow concurrent modification
		List tempList = new ArrayList(components);
		Iterator it = tempList.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			log.debug("Processing component: " + element);
			element.processRequest(request);
		}
	}
	
	/**
	 * Sets a reference to the form and registers the element by calling 
	 * {@link Form#registerElement(Element)}.
	 *  
	 * @throws IllegalStateException if form is null
	 */
	protected void initComponent(Element element) {
		if (getForm() == null) {
			throw new IllegalStateException("Form not set");
		}
		getForm().registerElement(element);
	}

	/**
	 * Called before processRequest() is invoked on the contained elements.
	 * Subclasses can override this method to perform custom processing. The
	 * default implementation does nothing.
	 */
	protected void processRequestInternal(HttpServletRequest request) {
	}

	protected boolean isSurroundBySpan() {
		return this.surroundBySpan;
	}

	protected void setSurroundBySpan(boolean surroundBySpan) {
		this.surroundBySpan = surroundBySpan;
	}

	protected void renderInternal(PrintWriter writer) {		
		if (surroundBySpan) {
			TagWriter spanTag = new TagWriter(writer);
			spanTag.start(Html.SPAN).attribute(Html.COMMON_ID, getId()).body();
			renderComponents(writer);
			spanTag.end();
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