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
package org.riotfamily.common.web.mvc.scope;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class FlashScopeArgumentResolver implements WebArgumentResolver {
	
	public Object resolveArgument(MethodParameter methodParameter,
			NativeWebRequest webRequest) throws Exception {
		
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		if (methodParameter.getParameterType().equals(FlashModel.class)) {
			return FlashModel.get(request);
		}
		FlashScopeAttribute annotation = getAnnotation(methodParameter.getParameterAnnotations());
		if (annotation != null) {
			FlashModel flashModel = FlashModel.get(request);
			if (flashModel != null) {
				String key = annotation.value();
				if (!StringUtils.hasLength(key)) {
					key = getRequiredParameterName(methodParameter);
				}
				
				return flashModel.get(key);
			}
			return null;
		}
		
		return UNRESOLVED;
	}
	
	private FlashScopeAttribute getAnnotation(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (FlashScopeAttribute.class.equals(annotation.annotationType())) {
				return (FlashScopeAttribute) annotation;
			}
		}
		return null;
	}
	
	private String getRequiredParameterName(MethodParameter methodParam) {
		String name = methodParam.getParameterName();
		if (name == null) {
			throw new IllegalStateException("No parameter name specified for argument of type [" +
					methodParam.getParameterType().getName() +
					"], and no parameter name information found in class file either.");
		}
		return name;
	}
	
}
