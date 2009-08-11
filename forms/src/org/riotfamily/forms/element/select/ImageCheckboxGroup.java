package org.riotfamily.forms.element.select;

import org.riotfamily.common.beans.property.PropertyUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ImageCheckboxGroup extends CheckboxGroup {

	private String imageProperty;
	
	public ImageCheckboxGroup() {
		setStyleClass("imageCheckboxGroup");
	}
	
	public void setImageProperty(String imageProperty) {
		this.imageProperty = imageProperty;
	}
	
	public String getInitScript() {
		StringBuffer sb = new StringBuffer(super.getInitScript());
		for (OptionItem option : getOptionItems()) {
			sb.append("new RiotImageCheckbox('");
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
