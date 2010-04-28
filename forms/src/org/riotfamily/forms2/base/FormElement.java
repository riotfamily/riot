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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.ExceptionUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.client.FormResource;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.client.IdGenerator;
import org.riotfamily.forms2.client.LoadingCodeGenerator;
import org.riotfamily.forms2.client.Resources;
import org.riotfamily.forms2.value.TypeInfo;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class FormElement extends ContainerElement {

	private List<Element> externalElements = Generics.newArrayList();
	
	/**
	 * Initializes all transient fields with the values from the given element.
	 */
	private void restoreTransientFields(FormElement source) {
		if (source != this) {
			Iterator<Element> it = source.getAllElements().iterator();
			Iterator<Element> dest = getAllElements().iterator();
			while (it.hasNext()) {
				copyTransientFields(it.next(), dest.next());
			}
		}
	}
	
	/**
	 * Copies the values of all transient fields from source to dest.
	 */
	private static void copyTransientFields(Element source, Element dest) {
		try {
			for (Class<?> c = dest.getClass(); c != null; c = c.getSuperclass()) {
				for (Field field : c.getDeclaredFields()) {
					if (Modifier.isTransient(field.getModifiers())) {
						ReflectionUtils.makeAccessible(field);
						Object value = field.get(dest);
						if (value == null) {
							Object originalValue = field.get(source);
							field.set(dest, originalValue);
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw ExceptionUtils.wrapReflectionException(e);
		}
	}
		
	public void addExternal(Element element) {
		externalElements.add(element);
	}
	
	private Collection<Element> getAllElements() {
		List<Element> result = Generics.newArrayList();
		result.add(this);
		collectElements(externalElements, result);
		collectElements(getChildElements(), result);
		return result;
	}
	
	private void collectElements(Collection<Element> childElements, List<Element> collection) {
		if (childElements != null) {
			collection.addAll(childElements);
			for (Element element : childElements) {
				collectElements(element.getChildElements(), collection);
			}
		}
	}
	
	@Override
	protected FormResource getResource() {
		return Resources.RIOT_FORMS;
	}
	
	private Collection<FormResource> getAllResources() {
		Set<FormResource> resources = Generics.newHashSet();
		for (Element element : getAllElements()) {
			Collection<FormResource> res = element.getResources();
			if (res != null) {
				resources.addAll(res);				
			}
		}
		return resources;
	}
			
	public State createAndInitState(Object object, Class<?> type) {
		if (object != null) {
			type = object.getClass();
		}
		State state = new State(new TypeInfo(type));
		for (Element element : externalElements) {
			state.register(element.createState(state));
		}
		state.setValue(object);
		return state;
	}
	
	public final class State extends ContainerElement.State implements FormState {

		private Serializable target;
		
		private Map<String, ElementState> statesById = Generics.newHashMap();
		
		private int elementCount = 0;

		private String contextPath = "";

		private String resourcePath = "/resources/";
		
		private IdGenerator idGenerator = new IdGenerator();

		private final TypeInfo typeInfo;

		State(TypeInfo typeInfo) {
			super(UUID.randomUUID().toString());
			this.typeInfo = typeInfo;
			init(null, this);
		}
			
		@Override
		public TypeInfo getTypeInfo() {
			return typeInfo;
		}
		
		@Override
		public Html newHtml() {
			return new Html(idGenerator);
		}
		
		@Override
		String register(ElementState state) {
			String id = "s" + elementCount++;
			statesById.put(id, state);
			return id;
		}
		
		public void setTarget(Serializable target) {
			this.target = target;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Serializable> T getTarget() {
			return (T) target;
		}
		
		public void setContextPath(String contextPath) {
			this.contextPath = contextPath;
		}
		
		public void setResourcePath(String resourcePath) {
			this.resourcePath = resourcePath;
		}
		
		public String resolveResource(String res) {
			return contextPath + resourcePath + res;
		}
		
		public ElementState getElementState(String id) {
			ElementState state = statesById.get(id);
			Assert.notNull(state, "No such state: '" + id + "'");
			return state;
		}
				
		@Override
		FormState getFormStateInternal() {
			return this;
		}
		
		public String render() {
			Html html = new Html(idGenerator);
			render(html);
			LoadingCodeGenerator.addLoadingCode(getAllResources(), contextPath + resourcePath, html);
			return html.inlineScripts().toString();
		}
				
		public void put(HttpSession session) {
			session.setAttribute(getAttributeName(id()), this);
		}
		
		public void remove(HttpSession session) {
			session.removeAttribute(getAttributeName(id()));
		}

		private FormElement getFormElement() {
			return FormElement.this;
		}
	}

	private static String getAttributeName(String id) {
		return FormState.class.getName() + '#' + id;
	}
	
	public FormState getState(HttpSession session, String id) {
		State state = (State) session.getAttribute(getAttributeName(id));
		state.getFormElement().restoreTransientFields(this);
		return state;
	}
}
