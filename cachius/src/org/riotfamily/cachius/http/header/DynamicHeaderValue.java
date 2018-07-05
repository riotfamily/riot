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

import javax.servlet.http.HttpServletRequest;

public abstract class DynamicHeaderValue implements HeaderValue {

	private String value;

	int insertAt;
	
	int skip;

	public DynamicHeaderValue(String value, int insertAt, int skip) {
		this.value = value;
		this.insertAt = insertAt;
		this.skip = skip;
	}

	public String resolve(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(value, 0, insertAt);
		appendDynamicValue(sb, request);
		if (insertAt + skip < value.length()) {
			sb.append(value, insertAt + skip, value.length());
		}
		return sb.toString();
	}
	
	
	public String getValue() {
		return value;
	}

	protected abstract void appendDynamicValue(StringBuilder sb, HttpServletRequest request);

}
