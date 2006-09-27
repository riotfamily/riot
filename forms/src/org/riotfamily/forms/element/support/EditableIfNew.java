package org.riotfamily.forms.element.support;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.element.ContainerElement;
import org.springframework.util.Assert;

public class EditableIfNew extends AbstractElement implements ContainerElement {
	
	private Editor editor;
		
	protected void afterFormSet() {
		Assert.notNull(editor, "An editor must be set.");
		getForm().registerElement(editor);
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
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

	public void processRequest(HttpServletRequest request) {
		if (getForm().isNew()) {
			editor.processRequest(request);
		}
	}
	
	public String getLabel() {
		return editor.getLabel();
	}
	
	protected void renderInternal(PrintWriter writer) {
		if (getForm().isNew()) {
			editor.render(writer);
		}
		else {
			Object value = editor.getValue();
			if (value != null) {
				TagWriter tag = new TagWriter(writer);
				tag.start(Html.SPAN);
				tag.attribute(Html.COMMON_CLASS, "read-only");
				tag.body(value.toString());
				tag.end();
			}
		}
	}

}
