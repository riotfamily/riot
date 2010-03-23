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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.client.FormResource;
import org.riotfamily.forms2.client.Resources;
import org.riotfamily.forms2.value.Value;
import org.riotfamily.forms2.value.ValueFactory;
import org.springframework.util.Assert;

public class FormElement extends ContainerElement {

	private int nextId = 0;
	
	private Map<String, Element> elementsById = Generics.newHashMap();
	
	private List<Element> externalElements = Generics.newArrayList();
	
	private Set<FormResource> resources = Generics.newHashSet();
	
	private ValueFactory valueFactory;

	public FormElement() {
		resources.add(Resources.RIOT_FORMS);
	}
	
	@Override
	public void register(Element element) {
		String id = "e" + nextId++;
		elementsById.put(id, element);
		element.setId(id);
		element.setRoot(this);
		Collection<FormResource> res = element.getResources();
		if (res != null) {
			resources.addAll(res);
		}
	}
	
	public void addExternal(Element element) {
		externalElements.add(element);
		element.setParent(this);
	}
	
	@Override
	public Collection<FormResource> getResources() {
		return resources;
	}
	
	public Element getElement(String id) {
		Element el = elementsById.get(id);
		Assert.notNull(el, "No such element: '" + id + "'");
		return el;
	}
	
	public ValueFactory getValueFactory() {
		return valueFactory;
	}
	
	@Override
	public FormElement getRoot() {
		return this;
	}
	
	@Override
	public FormState createAndInitState(ElementState parent, Value value) {
		FormState state = new FormState(value);
		state.init(this, null, state, value);
		for (Element element : externalElements) {
			state.register(element.createState(state, value));
		}
		return state;
	}
	
}
