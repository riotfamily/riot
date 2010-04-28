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
package org.riotfamily.pages.config;

import java.util.Collection;
import java.util.List;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.base.Element;

public abstract class AbstractPageType implements PageType {

	private String name;
	
	private String label;
	
	private Form form;
	
	private Object handler;
	
	private PageType parent;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		if (label == null) {
			label = FormatUtils.xmlToTitleCase(name);
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setElements(List<Object> elements) {
		addElements(elements);
	}
	
	@SuppressWarnings("unchecked")
	private void addElements(Collection<Object> elements) {
		for (Object element : elements) {
			if (element instanceof Element) {
				form.add((Element) element);
			}
			else if (element instanceof Collection) {
				addElements((Collection) element);
			}
			else {
				throw new IllegalArgumentException("Expected either an Element or a Collection: " + element);
			}
		}
	}

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}
		
	public List<? extends PageType> getChildTypes() {
		return null;
	}
	
	public PageType getParent() {
		return parent;
	}

	public void setParent(PageType parent) {
		this.parent = parent;
	}

	public void register(SitemapSchema schema, PageType parent) {
		schema.addType(this);
		setParent(parent);
		if (getChildTypes() != null) {
			for (PageType type : getChildTypes()) {
				type.register(schema, this);
			}
		}
	}
	
	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof PageType) {
			PageType other = (PageType) obj;
			return getName().equals(other.getName());
		}
		return false;
	}
	
}
