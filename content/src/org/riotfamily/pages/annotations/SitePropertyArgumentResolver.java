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
package org.riotfamily.pages.annotations;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Site;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class SitePropertyArgumentResolver implements WebArgumentResolver {

	public Object resolveArgument(MethodParameter methodParameter,
			NativeWebRequest webRequest) {
		
		SiteProperty annotation = getAnnotation(methodParameter); 
		if (annotation != null) {
			HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
			Site site = PageResolver.getSite(request);
			Assert.notNull(site, "No Site found for request");
			String name = getPropertName(annotation, methodParameter);
			return site.getProperty(name);
		}
		return UNRESOLVED;
	}
	
	private SiteProperty getAnnotation(MethodParameter param) {
		for (Annotation annotation : param.getParameterAnnotations()) {
			if (SiteProperty.class.equals(annotation.annotationType())) {
				return (SiteProperty) annotation;
			}
		}
		return null;
	}
	
	private String getPropertName(SiteProperty annotation, MethodParameter param) {
		String key = annotation.value();
		if (!StringUtils.hasLength(key)) {
			key = param.getParameterName();
			if (key == null) {
				throw new IllegalStateException("No property name specified for argument of type [" +
						param.getParameterType().getName() +
						"], and no parameter name information found in class file either.");
			}
		}
		return key;
	}
}
