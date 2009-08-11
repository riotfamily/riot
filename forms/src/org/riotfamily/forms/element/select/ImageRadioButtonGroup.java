package org.riotfamily.forms.element.select;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class ImageRadioButtonGroup extends RadioButtonGroup 
		implements DHTMLElement, ResourceElement {

	protected static final FormResource RESOURCE = new ScriptResource(
			"riot-js/checkbox.js", "RiotCheckboxGroup", Resources.PROTOTYPE);
	
	private String imageProperty;
	
	public ImageRadioButtonGroup() {
		setStyleClass("imageRadioButtonGroup");
	}
	
	public void setImageProperty(String imageProperty) {
		this.imageProperty = imageProperty;
	}
	
	public FormResource getResource() {
		return RESOURCE;
	}
	
	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		for (OptionItem option : getOptionItems()) {
			sb.append("new RiotImageRadioButton('");
			sb.append(option.getId());
			sb.append("'");
			if (imageProperty != null) {
				String image = PropertyUtils.getPropertyAsString(
						option.getObject(), imageProperty);
			
				if (image != null) { 
					sb.append(", '");
					sb.append(getFormContext().getContextPath());
					sb.append(image);
					sb.append("'");
				}
			}
			sb.append(");");
		}
		return sb.toString();
	}
	
}
