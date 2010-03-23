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
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.client.IdGenerator;
import org.riotfamily.forms2.client.LoadingCodeGenerator;
import org.riotfamily.forms2.value.Value;
import org.springframework.util.Assert;

public class FormState extends ContainerElement.State {

	private Class<?> type;
	
	private Serializable target;
	
	private Map<String, ElementState> statesById = Generics.newHashMap();
	
	private Map<String, ElementState> statesByElementId = Generics.newHashMap();
	
	private int elementCount = 0;

	private String contextPath = "";

	private String resourcePath = "/resources/";
	
	private IdGenerator idGenerator = new IdGenerator();
	
	FormState(Value value) {
		this.type = value.getTypeDescriptor().getType();
		setId(UUID.randomUUID().toString());
	}
	
	public Class<?> getType() {
		return type;
	}
		
	@Override
	public Html newHtml() {
		return new Html(idGenerator);
	}
	
	@Override
	public String register(ElementState state) {
		String id = "s" + elementCount++;
		statesById.put(id, state);
		statesByElementId.put(state.getElementId(), state);
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
	
	public ElementState getElementState(String id) {
		ElementState state = statesById.get(id);
		Assert.notNull(state, "No such state: '" + id + "'");
		return state;
	}
	
	@Override
	public FormState getFormState() {
		return this;
	}
	
	public ElementState getElementState(Element element) {
		String id = element.getId();
		ElementState state = statesByElementId.get(id);
		Assert.notNull(state, "No such state: '" + id + "'");
		return state;
	}
	
	public String render(FormElement formElement) {
		Html html = new Html(idGenerator);
		LoadingCodeGenerator loader = new LoadingCodeGenerator(formElement.getResources(), contextPath + resourcePath);
		html.script(loader.scripts());
		html.script(loader.stylesheets());
		Html div = html.div("form").id(getId());
		render(div, formElement);
		return html.toString();
	}
	
	// -----------------------------------------------------------------------
	// Session Binding
	// -----------------------------------------------------------------------
	
	public void put(HttpSession session) {
		session.setAttribute(getAttributeName(getId()), this);
	}
	
	public void remove(HttpSession session) {
		session.removeAttribute(getAttributeName(getId()));
	}

	private static String getAttributeName(String id) {
		return FormState.class.getName() + '#' + id;
	}
	
	public static FormState get(HttpSession session, String id) {
		return (FormState) session.getAttribute(getAttributeName(id));
	}

}