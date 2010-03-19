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
package org.riotfamily.forms2.base;

import java.io.Serializable;

import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;


public abstract class ElementState implements Serializable {

	private String id;
	
	private String elementId;
	
	private boolean enabled = true;
		
	private ElementState parent;
	
	private FormState formState;
	
	final void init(Element element, ElementState parent, FormState formState, Value value) {
		this.elementId = element.getId();
		this.parent = parent;
		this.formState = formState;
		if (parent != null) {
			setId(parent.register(this));
		}
		onInit(element, value);
	}
	
	final void setId(String id) {
		this.id = id;
	}
	
	public final String getId() {
		return id;
	}
	
	protected void onInit(Element element, Value value) {
	}

	public ElementState getParent() {
		return parent;
	}
	
	public FormState getFormState() {
		return formState;
	}
	
	public String register(ElementState state) {
		return parent.register(state);
	}
	
	public String getElementId() {
		return elementId;
	}
	
	public boolean isEnabled() {
		return enabled && (parent == null || parent.isEnabled());
	}
	
	protected Html newHtml() {
		return formState.newHtml();
	}
	
	public abstract void render(Html html, Element element);
	
	public abstract void populate(Value value, Element element);

}
