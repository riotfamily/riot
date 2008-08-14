/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.select;

import org.riotfamily.common.beans.PropertyUtils;

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
