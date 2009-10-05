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
package org.riotfamily.forms;

import java.io.PrintWriter;

import org.riotfamily.forms.request.FormRequest;





/**
 * Interface to be implemented by all form elements. If you want to create a 
 * new element you will most likely want to subclass one of the abstract base
 * classes in the <code>de.neteye.forms.element.support</code> package.
 */
public interface Element {
	
	/**
	 * Sets the element's unique id. Ids are assigned when an element is
	 * registered with a form.
	 * 
	 * @see Form#registerElement(Element)
	 */
	public void setId(String id);

	/**
	 * Returns the previously assigned id.
	 */
	public String getId();
	
	public String getEventTriggerId();
	
	/**
	 * Sets a reference to the form that contains the element.
	 */
	public void setForm(Form form);

	/**
	 * Returns the form that contains the element. 
	 */
	public Form getForm();
	
	/**
	 * Sets the FormContext. Invoked by {@link Form#registerElement(Element)}
	 * or {@link Form#setFormContext(FormContext)}.
	 */
	public void setFormContext(FormContext formContext);
	
	/**
	 * Returns the FormContext.
	 */
	public FormContext getFormContext();
	
	/**
	 * Sets the element's parent. E.g. the parent element is taken into account
	 * to determine the enabled state of the element.  
	 */
	public void setParent(Element parent);
	
	/**
	 * Returns the element's parent.
	 */
	public Element getParent();
	
	/**
	 * This method is invoked whenever a HTTP request needs to be processed.
	 * Elements may implement this method to change their internal state 
	 * according to parameters found in the request. 
	 */
	public void processRequest(FormRequest request);
		
	/**
	 * Renders the element to the given writer.
	 */
	public void render(PrintWriter writer);

	/**
	 * Returns the elements style class.  
	 */
	public String getStyleClass();

	/**
	 * Focuses the element.
	 */
	public void focus();
	
	/**
	 * Returns whether the element will accept user input. The state should
	 * be considered during rendering, i.e. disabled elements should look
	 * different than enabled ones.
	 */
	public boolean isEnabled();
	
	/**
	 * Enables (or disables) the element.
	 */
	public void setEnabled(boolean enabled);
	
	/**
	 * Returns whether the element is mandatory and must be filled out by 
	 * the user.
	 */
	public boolean isRequired();
	
	/**
	 * Sets whether the element is required.
	 */
	public void setRequired(boolean required);
	
	public boolean isVisible();
		
	/**
	 * Returns whether the element is composed of multiple widgets. 
	 * The information may be used by templates to render composite elements
	 * in the same style as element groups or nested forms. 
	 */
	public boolean isCompositeElement();
	
}