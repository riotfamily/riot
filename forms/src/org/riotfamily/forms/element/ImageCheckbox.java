package org.riotfamily.forms.element;

import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;

public class ImageCheckbox extends Checkbox 
		implements DHTMLElement, ResourceElement  {
	
	private static final String STYLE_CLASS = "hidden";
	
	protected static final FormResource RESOURCE = new ScriptResource(
			"riot-js/checkbox.js", "RiotImageCheckbox", Resources.PROTOTYPE);
	
	public ImageCheckbox() {
		setStyleClass(STYLE_CLASS);
	}
	
	public FormResource getResource() {
		return RESOURCE;
	}
		
	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		sb.append("new RiotImageCheckbox('");
		sb.append(getId());
		sb.append("', '");
		sb.append(getEditorBinding().getProperty());
		sb.append("');");
		return sb.toString();
	}

}
