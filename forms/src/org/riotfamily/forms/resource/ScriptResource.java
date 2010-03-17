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
package org.riotfamily.forms.resource;

import java.util.Arrays;
import java.util.Collection;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.util.Assert;

/**
 *
 */
public class ScriptResource implements FormResource {
	
	private String url;
	
	private String test;
	
	private Collection<FormResource> dependencies;
	
	public ScriptResource(String url) {
		this(url, null);
	}
	
	public ScriptResource(String url, String test, FormResource... dependencies) {
		Assert.notNull(url);
		this.url = url;
		this.test = test;
		if (dependencies != null && dependencies.length > 0) {
			this.dependencies = Arrays.asList(dependencies);
		}
	}
	
	public String getUrl() {
		return this.url;
	}

	public String getTest() {
		return this.test;
	}

	public Collection<FormResource> getDependencies() {
		return this.dependencies;
	}

	public void accept(ResourceVisitor vistor) {
		vistor.visitScript(this);
	}
	
	@Override
	public String toString() {
		return FormatUtils.toJSON(this);
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof ScriptResource) {
			ScriptResource other = (ScriptResource) obj;
			return this.url.equals(other.url);
		}
		return false;
	}
}
