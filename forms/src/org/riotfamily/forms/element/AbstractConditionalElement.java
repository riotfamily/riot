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
package org.riotfamily.forms.element;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.riotfamily.forms.AbstractElement;
import org.riotfamily.forms.ContainerElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.ui.Dimension;
import org.springframework.util.Assert;

public abstract class AbstractConditionalElement extends AbstractElement 
		implements ContainerElement {
	
	private Editor editor;
	
	private boolean hide;

	@Override
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
	 * Sets whether the editor should be hidden if it is not editable.
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
	
	public List<Element> getElements() {
		return Collections.singletonList((Element) editor);
	}

	@Override
	public void processRequest(FormRequest request) {
		if (isEditable() && isEnabled()) {
			editor.processRequest(request);
		}
	}
	
	@Override
	public String getLabel() {
		if (!hide || isEditable()) {
			return editor.getLabel();
		}
		return null;		
	}
	
	@Override
	public boolean isRequired() {
		if (isEditable()) {
			return editor.isRequired();
		}
		return false;
	}
	
	@Override
	protected void renderInternal(PrintWriter writer) {
		if (!hide || isEditable()) {
			editor.render(writer);
			editor.getForm().getErrors().renderErrors(editor);
		}
	}
	
	public Dimension getDimension() {
		if (!hide || isEditable()) {
			return editor.getDimension();
		}
		return null;
	}

	@Override
	public String getStyleClass() {
		if (!hide || isEditable()) {
			return editor.getStyleClass();
		}
		return null;
	}

	@Override
	public boolean isCompositeElement() {
		return editor.isCompositeElement();
	}
	
	@Override
	public final boolean isEnabled() {
		return isEditable();
	}
	
	protected abstract boolean isEditable();

}
