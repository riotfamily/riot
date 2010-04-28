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
package org.riotfamily.forms.option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.riotfamily.common.util.ExceptionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class DefaultOptionCreator implements OptionCreator {

	private static DefaultOptionCreator instance = new DefaultOptionCreator();
	
	public static DefaultOptionCreator defaultInstance() {
		return instance;
	}
	
	protected DefaultOptionCreator() {
	}
	
	public Option createOption(Object object) {
		if (object instanceof Option) {
			return (Option) object;
		}
		Object value = getValue(object, OptionValue.class, object);
		Object label = getValue(object, OptionLabel.class, object);
		return new Option(value, label);
	}
	
	protected Object getValue(Object object, Class<? extends Annotation> annotationClass, Object defaultValue) {
		try {
			for (Method method : object.getClass().getMethods()) {
				if (method.getAnnotation(annotationClass) != null) {
					Assert.isTrue(method.getParameterTypes().length == 0);
					return method.invoke(object);
				}
			}
			
			for (Field field : object.getClass().getDeclaredFields()) {
				if (field.getAnnotation(annotationClass) != null) {
					ReflectionUtils.makeAccessible(field);
					return field.get(object);
				}
			}
			return defaultValue;
		}
		catch (Exception e) {
			return ExceptionUtils.wrapReflectionException(e);
		}
	}

}
