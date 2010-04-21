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
package org.riotfamily.forms2.element.support;

import java.io.Serializable;

public class Option implements Serializable {

	private String groupName;
	
	private String value;
	
	private String label;
	
	private boolean selected;
	
	private Serializable reference;
	
	public Option(String groupName, Serializable reference, String value, String label, boolean selected) {
		this.groupName = groupName;
		this.reference = reference;
		this.value = value;
		this.label = label;
		this.selected = selected;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public Serializable getReference() {
		return reference;
	}
	
	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
}