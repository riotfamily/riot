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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.select;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.ContainerElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.riotfamily.forms.request.FormRequest;

public class ElementSwitch extends CompositeElement implements ContainerElement {
	
	private AbstractSingleSelectElement selectElement;
	
	private TemplateElement templateElement;
	
	private List<Element> elements = new ArrayList<Element>();
	
	public ElementSwitch() {
		templateElement = new TemplateElement("chooseableElement");
		templateElement.setTemplate(TemplateUtils.getTemplatePath(this.getClass()));
		addComponent(templateElement);
	}

	public void addElement(Element element) {
		if (selectElement == null) {
			selectElement = (AbstractSingleSelectElement) element;
			selectElement.addChangeListener(new ChangeListener() {
			
				public void valueChanged(ChangeEvent event) {					
					getForm().getFormListener().elementChanged(ElementSwitch.this);					
				}
			});
			templateElement.setAttribute("select", element);
		}
		else {
			elements.add(element);
		}
		
	}
	
	public List<Element> getElements() {		
		return null;
	}

	public void removeElement(Element element) {		
	}
	
	@Override
	protected void initCompositeElement() {		
		initComponent(selectElement);
		for (Element element : elements) {
			initComponent(element);
		}
	}
	
	@Override
	protected void renderInternal(PrintWriter writer) {
		int selectedIndex = selectElement.getSelectedIndex();
		if (selectedIndex == -1) {
			selectedIndex = 0;
		}
		Editor element =(Editor) elements.get(selectedIndex);
		String property = element.getEditorBinding().getProperty();
		element.getEditorBinding().getEditorBinder().bind(element, property);
		templateElement.setAttribute("element", element);
		super.renderInternal(writer);
	}
	
	@Override
	protected void processRequestInternal(FormRequest request) {		
		selectElement.processRequest(request);
		int selectedIndex = selectElement.getSelectedIndex();
		elements.get(selectedIndex).processRequest(request);
	}
	
	@Override
	public boolean isCompositeElement() {		
		return false;
	}
	
}
