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
package org.riotfamily.core.resource;

import java.io.FilterReader;
import java.io.Reader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.io.PropertyFilterReader;
import org.riotfamily.common.util.Generics;
import org.springframework.web.servlet.support.RequestContextUtils;

public class PropertyResourceFilter extends AbstractPathMatchingResourceFilter {

	public static final String CONTEXT_PATH_PROPERTY = "contextPath";

	public static final String LANGUAGE_PROPERTY = "language";

	private Map<String, String> properties;
	
	private boolean exposeContextPath = true;
	
	private boolean exposeLanguage = true;
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
		
	public void setExposeContextPath(boolean exposeContextPath) {
		this.exposeContextPath = exposeContextPath;
	}
	
	public void setExposeLanguage(boolean exposeLanguage) {
		this.exposeLanguage = exposeLanguage;
	}

	public FilterReader createFilterReader(Reader in, HttpServletRequest request) {
		Map<String, String> props = Generics.newHashMap(properties);
		if (exposeContextPath) {
			props.put(CONTEXT_PATH_PROPERTY, request.getContextPath());
		}
		if (exposeLanguage) {
			props.put(LANGUAGE_PROPERTY,
					RequestContextUtils.getLocale(request).getLanguage().toLowerCase());
		}
		return new PropertyFilterReader(in, props);
	}

}
