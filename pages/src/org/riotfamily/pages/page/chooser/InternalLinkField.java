package org.riotfamily.pages.page.chooser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.core.TextField;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.StylesheetResource;
import org.riotfamily.forms.template.TemplateUtils;

public class InternalLinkField extends TextField implements ResourceElement,
	DHTMLElement {
	
	private static final List RESOURCES = new ArrayList();
	
	static {
		RESOURCES.add(Resources.PROTOTYPE);
		RESOURCES.add(Resources.RIOT_WINDOW_CALLBACK);
		RESOURCES.add(new StylesheetResource("internal-link-field.css"));
	}
	
	private String linkSuffix;
	
	private String chooserUrl = "/riot/pages/chooser";
	

	public InternalLinkField() {
		setStyleClass("text internal-link");
	}
	
	public String getChooserUrl() {
		return this.chooserUrl;
	}

	public void setChooserUrl(String chooserUrl) {
		this.chooserUrl = chooserUrl;
	}

	public String getLinkSuffix() {
		return this.linkSuffix;
	}

	public void setLinkSuffix(String linkSuffix) {
		this.linkSuffix = linkSuffix;
	}

	public Collection getResources() {
		return RESOURCES;
	}
	
	public String getInitScript() {
		return TemplateUtils.getInitScript(this);
	}
	
	public String getButtonId() {
		return getId() + "-button";
	}
	
	public void renderInternal(PrintWriter writer) {
		DocumentWriter doc = new DocumentWriter(writer);
		doc.start(Html.DIV).attribute(Html.COMMON_CLASS, "internal-link");
		doc.start(Html.DIV).attribute(Html.COMMON_CLASS, "input-wrapper");
		doc.body();
		
		super.renderInternal(writer);
		
		doc.end();
		
		doc.startEmpty(Html.INPUT)
				.attribute(Html.INPUT_TYPE, "button")
				.attribute(Html.INPUT_VALUE, "Internal ...")
				.attribute(Html.COMMON_ID, getButtonId())
				.attribute(Html.COMMON_CLASS, "button")
				.closeAll();
	}

}
