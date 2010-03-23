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

import org.riotfamily.common.util.ExceptionUtils;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;
import org.springframework.util.ClassUtils;


public abstract class ElementState implements Serializable {

	private String id;
	
	private String elementId;
	
	private boolean enabled = true;
		
	private ElementState parent;
	
	private FormState formState;
	
	static ElementState create(final Class<?> elementClass) {
		try {
			for(Class<?> c = elementClass; c != null; c = c.getSuperclass()) {
				for (Class<?> innerClass : c.getDeclaredClasses()) {
					if (ElementState.class.isAssignableFrom(innerClass)) {
						return innerClass.asSubclass(ElementState.class).newInstance();
					}
				}
			}
			throw new IllegalStateException(elementClass + " does not declare a State class");
		}
		catch (Exception e) {
			throw ExceptionUtils.wrapReflectionException(e);
		}
	}
	
	/**
	 * Called by {@link Element#createAndInitState}.
	 */
	final void init(Element element, ElementState parent, FormState formState, Value value) {
		this.elementId = element.getId();
		this.parent = parent;
		this.formState = formState;
		if (parent != null) {
			setId(parent.register(this));
		}
		onInit(element, value);
	}
	
	protected final void setId(String id) {
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
	
	public final void render(Html html, Element element) {
		renderElement(wrap(html, element), element);
	}
	
	Html wrap(Html html, Element element) {
		return html.div("state").id(getId())
				.addClass(ClassUtils.getShortName(element.getClass()));
	}

	protected abstract void renderElement(Html html, Element element);
	
	public abstract void populate(Value value, Element element);

}
