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
package org.riotfamily.forms.request;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class SimpleFormRequest implements FormRequest {

	private Map<String, String> params;
	
	public SimpleFormRequest(Map<String, String> params) {
		if (params != null) {
			this.params = params;
		}
		else {
			this.params = Collections.emptyMap();
		}
	}

	public MultipartFile getFile(String name) {
		Object value = params.get(name);
		if (value instanceof MultipartFile) {
			return (MultipartFile) value;
		}
		return null;
	}

	public String getParameter(String name) {
		Object value = params.get(name);
		if (value instanceof String[]) {
			String[] values = (String[]) value;
			return values.length > 0 ? values[0] : null;
		}
		if (value instanceof Collection<?>) {
			Iterator<?> it  = ((Collection<?>) value).iterator();
			return it.hasNext() ? (String) it.next() : null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String[] getParameterValues(String name) {
		Object value = params.get(name);
		if (value instanceof String[]) {
			return (String[]) value;
		}
		if (value instanceof Collection<?>) {
			return StringUtils.toStringArray((Collection<String>) value);
		}
		if (value instanceof String) {
			return new String[] { (String) value };
		}
		return null;
	}

}
