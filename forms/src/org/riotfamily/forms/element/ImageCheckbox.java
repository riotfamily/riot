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
			"riot/checkbox.js", "RiotImageCheckbox", Resources.PROTOTYPE);
	
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
