package org.riotfamily.forms.element.support;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.template.TemplateUtils;


/**
 * CompositeElement that is rendered using a template.
 */
public class TemplateElement extends CompositeElement {
	
	private Map renderModel = new HashMap();
	
	private String template;
	
	public TemplateElement() {
		this("element");
	}
	
	public TemplateElement(String modelKey) {
		template = TemplateUtils.getTemplatePath(this);
		setAttribute(modelKey, this);
	}
		
	protected void addComponent(String key, Element element) {
		addComponent(element);
		setAttribute(key, element);
	}
	
	protected Map getRenderModel() {
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
	 * @see org.riotfamily.forms.template.TemplateRenderer
	 */
	public final void setTemplate(String name) {
		this.template = name;
	}
	
	protected void renderComponents(PrintWriter writer) {
		if (isSurroundBySpan()) {
			TagWriter spanTag = new TagWriter(writer);
			spanTag.start(Html.SPAN).attribute(Html.COMMON_ID, getId()).body();
			renderTemplate(writer);
			spanTag.end();
		}
		else {
			renderTemplate(writer);
		}
	}
	
	protected void renderTemplate(PrintWriter writer) {
		getFormContext().getTemplateRenderer().render(
				template, renderModel, writer);
	}
}
