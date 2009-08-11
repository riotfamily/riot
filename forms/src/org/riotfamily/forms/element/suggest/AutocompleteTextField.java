package org.riotfamily.forms.element.suggest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.element.AbstractTextElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class AutocompleteTextField extends AbstractTextElement 
		implements ResourceElement, DHTMLElement, ContentElement {

	private AutocompleterModel model;

	public void setModel(AutocompleterModel model) {
		this.model = model;
	}

	public FormResource getResource() {
		return Resources.SCRIPTACULOUS_CONTROLS;
	}
	
	public void renderInternal(PrintWriter writer) {
		super.renderInternal(writer);
		TagWriter tag = new TagWriter(writer);
		tag.start("div").attribute("id", getChoicesDivId())
				.attribute("class", "autocomplete")
				.attribute("style", "display:none")
				.end();
	}
	
	private String getChoicesDivId() {
		return getId() + "-choices";
	}
	
	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		sb.append("new Ajax.Autocompleter(\"").append(getEventTriggerId()).append("\", \"")
			.append(getChoicesDivId()).append("\", \"")
			.append(getFormContext().getContentUrl(this)).append("\", {});");
		
		return sb.toString();
	}
		
	public void handleContentRequest(HttpServletRequest request, 
			HttpServletResponse response) throws IOException {

		response.setContentType("text/html");
		String search = request.getParameter(getParamName());
		DocumentWriter doc = new DocumentWriter(response.getWriter());
		doc.start("ul");
		for (String value : model.getSuggestions(search, this, request)) {
			doc.start("li").body(value).end();
		}
		doc.end();
	}
}
