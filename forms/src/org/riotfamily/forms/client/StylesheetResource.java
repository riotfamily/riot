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
package org.riotfamily.forms.client;

import org.codehaus.jackson.annotate.JsonValue;
import org.springframework.util.Assert;

/**
 *
 */
public class StylesheetResource implements FormResource {

	private String url;
	
	public StylesheetResource(String url) {
		Assert.notNull(url);
		this.url = url;
	}
	
	@JsonValue
	public String getUrl() {
		return this.url;
	}

	public void accept(ResourceVisitor visitor) {
		visitor.visitStyleSheet(this);
	}
	
	public int hashCode() {
		return url.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof StylesheetResource) {
			StylesheetResource other = (StylesheetResource) obj;
			return this.url.equals(other.url);
		}
		return false;
	}

}