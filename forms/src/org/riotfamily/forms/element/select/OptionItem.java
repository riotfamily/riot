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

/**
 *
 */
public class OptionItem {

	private Object object;
	
	private Object value;

	private String label;
	
	private String styleClass;

	private SelectElement parent;
	
	public OptionItem(Object object, Object value, String label, String styleClass, SelectElement parent) {
		this.object = object;
		this.value = value;
		this.label = label;
		this.styleClass = styleClass;
		this.parent = parent;
	}

	public SelectElement getParent() {
		return parent;
	}
	
	public boolean isSelected() {
		return parent.isSelected(this);
	}

	public int getIndex() {
		return parent.getOptionIndex(this);
	}

	public String getLabel() {
		return label;
	}

	public Object getValue() {
		return value;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public String getId() {
		return parent.getId() + '-' + getIndex();
	}
	
	public String getStyleClass() {
		return styleClass;
	}

	public void render() {
		parent.renderOption(this);
	}

}