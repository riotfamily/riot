package org.riotfamily.forms.element;

import java.io.PrintWriter;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.TemplateUtils;


/**
 * CompositeElement that is rendered using a template.
 */
public class TemplateElement extends CompositeElement {
	
	private Map<String, Object> renderModel = Generics.newHashMap();
	
	private String template;
	
	public TemplateElement() {
		this("element");
	}
	
	public TemplateElement(String modelKey) {
		this.template = TemplateUtils.getTemplatePath(this);
		setAttribute(modelKey, this);
	}
	
	public TemplateElement(String modelKey, String template) {
		this.template = template;
		setAttribute(modelKey, this);
	}
		
	protected void addComponent(String key, Element element) {
		addComponent(element);
		setAttribute(key, element);
	}
	
	public Map<String, Object> getRenderModel() {
		return renderModel;
	}
	
	public void setAttribute(String key, Object value) {
		renderModel.put(key,value);
	}
	
	public Object getAttribute(String key) {
		return renderModel.get(key);
	}
	
	/**
	 * Returns the name of the template that is used to render the element.
	 */
	protected final String getTemplate() {
		return template;
	}

	/**
	 * Sets the name of the template that is used to render the element.
	 * 
	 * @param name name of the template to use
	 * @see #renderInternal(PrintWriter)
	 * @see org.riotfamily.forms.TemplateRenderer
	 */
	public final void setTemplate(String name) {
		this.template = name;
	}
	
	protected void afterFormContextSet() {
		setAttribute("messageResolver", getFormContext().getMessageResolver());
		setAttribute("errors", getForm().getErrors());
	}
	
	protected void renderInternal(PrintWriter writer) {
		renderTemplate(writer);
	}
	
	protected void renderTemplate(PrintWriter writer) {
		getFormContext().getTemplateRenderer().render(
				template, renderModel, writer);
	}
}
