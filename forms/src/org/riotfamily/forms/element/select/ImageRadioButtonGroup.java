/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
			"riot/checkbox.js", "RiotCheckboxGroup", Resources.PROTOTYPE);
	
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
