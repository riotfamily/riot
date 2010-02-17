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
package org.riotfamily.cachius.http.header;

import java.io.Serializable;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class Header implements Serializable {
	
	private static final long serialVersionUID = -4213831361339746664L;

	private String name;
	
	private HeaderValue value;
	
	private ArrayList<HeaderValue> additionalValues = new ArrayList<HeaderValue>();

	public Header(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setValue(HeaderValue value) {
		additionalValues.clear();
		this.value = value;
	}
	
	public void addValue(HeaderValue value) {
		additionalValues.add(value);
	}
	
	public void send(HttpServletRequest request, HttpServletResponse response) {
		if (value != null) {
			response.setHeader(name, value.resolve(request));
		}
		for (HeaderValue value : additionalValues) {
			response.addHeader(name, value.resolve(request));	
		}
	}
	
}