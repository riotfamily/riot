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
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class Form {

	private FormElement formElement = new FormElement();
	
	private ConversionService conversionService = ConversionServiceFactory.createDefaultConversionService();
	
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
		Value value = ValueFactory.createValue(object, type, false);
		return formElement.createAndInitState(null, value);
	}
	
	public Object populate(Object object, FormState formState) {
		Value value = ValueFactory.createValue(object, formState.getType(), false);
		formState.populate(value);
		return value.get();
	}
	
	public String render(FormState formState) {
		return formState.render();
	}
	
	public FormState getState(HttpSession session, ClientEvent event) {
		return formElement.getState(session, event.getFormId());
	}
	
	public List<Action> dispatchEvent(HttpSession session, ClientEvent event) {
		return dispatchEvent(getState(session, event), event);	
	}
	
	public List<Action> dispatchEvent(FormState formState, ClientEvent event) {
		ElementState elementState = formState.getElementState(event.getStateId());
		UserInterface ui = new UserInterface();
		invoke(elementState, event.getHandler(), ui, event.getFileOrValue());
		return ui.getActions();
	}
	
	private void invoke(Object obj, String name, Object... params) {
		Method method = getMethod(obj.getClass(), name);
		checkAndConvertTypes(params, method.getParameterTypes());				
		ReflectionUtils.invokeMethod(method, obj, params);
	}
	
	private static Method getMethod(Class<?> clazz, String name) {
		Method result = null;
		for (Method method : clazz.getMethods()) {
			if (name.equals(method.getName())) {
				Assert.isNull(result, "Class" + clazz + " must not define more than one method named " + name);
				result = method;
			}
		}
		Assert.notNull(result, "No such method '" + name + "' in " + clazz);
		return result;
	}

	private void checkAndConvertTypes(Object[] params, Class<?>[] types) {
		Assert.isTrue(params.length == types.length, "Wrong number of parameters");
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			if (param != null && !types[i].isInstance(param)) {
				Assert.isTrue(conversionService.canConvert(param.getClass(), types[i]), 
						"Cant convert " + param.getClass() + " into " + types[i]);
				
				params[i] = conversionService.convert(param, types[i]);
			}
		}
	}
	
}
