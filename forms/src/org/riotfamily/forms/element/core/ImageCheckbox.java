package org.riotfamily.forms.element.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.ScriptSequence;

public class ImageCheckbox extends Checkbox 
		implements DHTMLElement, ResourceElement  {
	
	private static final List RESOURCES = Collections.singletonList(
			new ScriptSequence(new ScriptResource[] {
				Resources.PROTOTYPE, Resources.RIOT_IMAGE_CHECKBOX 
			}));
	
	private static final String STYLE_CLASS = "hidden";
	
	public ImageCheckbox() {
		setStyleClass(STYLE_CLASS);
	}
	
	public Collection getResources() {
		return RESOURCES;
	}
	
	public String getPrecondition() {		
		return "ImageCheckbox";
	}
	
	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		sb.append("new ImageCheckbox('");
		sb.append(getId());
		sb.append("', '");
		sb.append(getEditorBinding().getProperty());
		sb.append("');");
		return sb.toString();
	}

}
