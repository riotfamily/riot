package org.riotfamily.forms.element.core;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.ajax.JavaScriptEvent;
import org.riotfamily.forms.bind.BeanEditor;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.bind.EditorBinder;
import org.riotfamily.forms.bind.MapOrBeanWrapper;
import org.riotfamily.forms.element.ContainerElement;
import org.riotfamily.forms.element.support.AbstractEditorBase;
import org.riotfamily.forms.element.support.Container;
import org.riotfamily.forms.element.support.TemplateElement;
import org.riotfamily.forms.i18n.MessageUtils;
import org.riotfamily.forms.template.TemplateUtils;

/**
 * Element to edit nested beans.
 */
public class NestedForm extends TemplateElement implements 
		ContainerElement, Editor, BeanEditor {
		
	private EditorBinder editorBinder;

	private Container elements = new Container();
	
	private boolean present;
	
	private boolean indent = true;
	
	private String buttonLabelKeySet = "form.nestedForm.set";
	
	private String buttonLabelKeyRemove = "form.nestedForm.remove";
	
	public NestedForm() {
		super("form");
		addComponent("elements", elements);
		addComponent("toggleButton", new ToggleButton());
	}
	
	public void setIndent(boolean indent) {
		this.indent = indent;
		setTemplate(TemplateUtils.getTemplatePath(NestedForm.class, 
				indent ? null : "_noindent"));
	}	

	protected void setEditorBinder(EditorBinder editorBinder) {
		this.editorBinder = editorBinder;
	}
	
	public void setBeanClass(Class beanClass) {
		EditorBinder editorBinder = new EditorBinder(new MapOrBeanWrapper(beanClass));
		setEditorBinder(editorBinder);
	}
	
	/**
	 * Invoked by {@link AbstractEditorBase#setEditorBinding} when the nested 
	 * form is bound to a property.
	 */
	protected void afterBindingSet() {
		if (editorBinder == null) {
			setBeanClass(getEditorBinding().getPropertyType());
		}
		editorBinder.setParent(getEditorBinding());
	}
	
	public Editor getEditor(String property) {
		return editorBinder.getEditor(property);
	}
	
	protected void afterFormContextSet() {		
		editorBinder.registerPropertyEditors(
				getFormContext().getPropertyEditorRegistrars());
	}
	
	public String getLabel() {
		return indent ? super.getLabel() : null;
	}
	
	public boolean isPresent() {
		return present;
	}
	
	protected void toggle() {
		present = !present;
		if (getFormListener() != null) {
			getFormListener().elementChanged(this);			
		}
	}
	
	public void processRequest(HttpServletRequest request) {
		if (present || isRequired()) {
			super.processRequest(request);
		}
	}
	
	/**
	 * Sets the given value as backingObject on the internal EditorBinder and
	 * initializes the bound editors.
	 * 
	 * @see org.riotfamily.forms.bind.Editor#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		this.present = isRequired() || value != null;		
		editorBinder.setBackingObject(value);
		editorBinder.initEditors();
	}

	/**
	 * Invokes the EditorBinder to populate the backingObject and returns the
	 * populated instance.
	 * 
	 * @see org.riotfamily.forms.bind.Editor#getValue()
	 */
	public Object getValue() {
		// TODO: Revisit, present is false if used as DynamicList Element
		if (present || isRequired()) {			
			return editorBinder.populateBackingObject();
		}
		return null;
	}

	public void bind(Editor editor, String property) {
		editorBinder.bind(editor, property);
	}

	public void addElement(Element element) {
		elements.addElement(element);
	}
	
	public void addElement(Editor element, String property) {
		addElement(element);
		bind(element, property);
	}
	
	public void removeElement(Element element) {
		elements.removeElement(element);
	}

	public String getProperty() {
		if (getEditorBinding() == null) {
			return null;
		}
		return getEditorBinding().getProperty();
	}

	private class ToggleButton extends Button {
		
		private ToggleButton() {
			setCssClass("button button-toggle");
			setTabIndex(2);
		}
		
		public String getLabel() {
			String key = present ? buttonLabelKeyRemove : buttonLabelKeySet;
			String label = MessageUtils.getMessage(this, key);
			if (label == null) {
				label = present ? "Remove" : "Set";
			}
			return label;
		}

		protected void onClick() {
			toggle();
		}
		
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}
}