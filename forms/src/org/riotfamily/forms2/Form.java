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
package org.riotfamily.forms2;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.FormElement;
import org.riotfamily.forms2.base.FormState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.Action;
import org.riotfamily.forms2.client.ClientEvent;
import org.riotfamily.forms2.value.Value;
import org.riotfamily.forms2.value.ValueFactory;
import org.springframework.util.ReflectionUtils;

public class Form {

	private FormElement formElement = new FormElement();
	
	public Form add(Element element) {
		formElement.add(element);
		return this;
	}
	
	public Form addExternal(Element element) {
		formElement.addExternal(element);
		return this;
	}
	
	public FormState createState(Object object, Class<?> type) {
		if (object != null) {
			type = object.getClass();
		}
		Value value = ValueFactory.createValue(object, type);
		return formElement.createState(value);
	}
	
	public Object populate(Object object, FormState formState) {
		Value value = ValueFactory.createValue(object, formState.getType());
		formState.populate(value, formElement);
		return value.get();
	}
	
	public String render(FormState formState) {
		return formState.render(formElement);
	}
	
	public List<Action> dispatchEvent(HttpSession session, ClientEvent event) {
		return dispatchEvent(FormState.get(session, event.getFormId()), event);	
	}
	
	public List<Action> dispatchEvent(FormState formState, ClientEvent event) {
		ElementState elementState = formState.getElementState(event.getStateId());
		Element element = formElement.getElement(elementState.getElementId());
		UserInterface ui = new UserInterface();
		invoke(elementState, event.getHandler(), ui, element, event.getFileOrValue());
		return ui.getActions();
	}
	
	private static void invoke(Object obj, String name, Object... params) {
		for (Method method : obj.getClass().getMethods()) {
			if (name.equals(method.getName()) && typesAreCompatible(params, method.getParameterTypes())) {
				ReflectionUtils.invokeMethod(method, obj, params);
				return;
			}
		}
		throw new IllegalArgumentException("No such method '" + name + "' in " + obj.getClass());
	}

	private static boolean typesAreCompatible(Object[] params, Class<?>[] types) {
		if (params.length != types.length) {
			return false;
		}
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			if (param != null && !types[i].isInstance(param)) {
				return false;
			}
		}
		return true;
	}
	
}
