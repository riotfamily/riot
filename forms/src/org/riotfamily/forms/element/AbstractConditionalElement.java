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
package org.riotfamily.forms.element;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.riotfamily.forms.AbstractElement;
import org.riotfamily.forms.ContainerElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.request.FormRequest;
import org.springframework.util.Assert;

public abstract class AbstractConditionalElement extends AbstractElement 
		implements ContainerElement {
	
	private Editor editor;
	
	private boolean hide;

	protected void afterFormSet() {
		Assert.notNull(editor, "An editor must be set.");
		getForm().registerElement(editor);
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}
	
	protected Editor getEditor() {
		return editor;
	}
	
	/**
	 * Sets whether the editor should be hidden if the form is not new.
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	
	/**
	 * Implementation of the ContainerElement interface. This allows us to
	 * specify the editor as nested node in the XML configuration.
	 * 
	 * @see org.riotfamily.riot.editor.xml.XmlEditorRepositoryDigester
	 * @see ContainerElement#addElement(Element)
	 */
	public void addElement(Element element) {
		Assert.isInstanceOf(Editor.class, element, "Element must be an Editor.");
		Assert.isTrue(editor == null, "Only one child element is allowed.");
		setEditor((Editor) element);
	}
	
	public void removeElement(Element element) {
		if (element == editor) {
			setEditor(null);
		}
	}
	
	public List getElements() {
		return Collections.singletonList(editor);
	}

	public void processRequest(FormRequest request) {
		if (isEditable()) {
			editor.processRequest(request);
		}
	}
	
	public String getLabel() {
		if (hide && !getForm().isNew()) {
			return null;
		}
		return editor.getLabel();
	}
	
	public boolean isRequired() {
		if (isEditable()) {
			return editor.isRequired();
		}
		return false;
	}
	
	protected void renderInternal(PrintWriter writer) {
		if (isEditable()) {
			editor.setEnabled(true);
			editor.render(writer);
		}
		else if (!hide) {
			editor.setEnabled(false);
			editor.render(writer);
		}
	}

	public String getStyleClass() {
		if (!hide) {
			return editor.getStyleClass();
		}
		return null;
	}

	public boolean isCompositeElement() {
		return editor.isCompositeElement();
	}
	
	protected abstract boolean isEditable();

}
