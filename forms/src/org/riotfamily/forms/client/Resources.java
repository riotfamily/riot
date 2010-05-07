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

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.riotfamily.common.util.Generics;


public class Resources implements Serializable {

	private Set<ScriptResource> scripts = Generics.newLinkedHashSet();
	
	private Set<String> stylesheets = Generics.newLinkedHashSet();

	public Resources() {
	}
	
	public Resources(Resources other) {
		add(other);
	}

	public Resources add(Resources other) {
		if (other != null) { 
			scripts.addAll(other.scripts);
			stylesheets.addAll(other.stylesheets);
		}
		return this;
	}
	
	public Resources script(String url) {
		scripts.add(new ScriptResource(url));
		return this;
	}
	
	public Resources script(String url, String test) {
		scripts.add(new ScriptResource(url, test));
		return this;
	}
	
	public Resources stylesheet(String url) {
		stylesheets.add(url);
		return this;
	}
	
	public Collection<ScriptResource> getScripts() {
		return scripts;
	}
	
	public Collection<String> getStylesheets() {
		return stylesheets;
	}
	
}
