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
package org.riotfamily.forms.element;

import java.util.Collection;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.AbstractElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;

public abstract class AbstractResourceElement extends AbstractElement 
		implements ResourceElement {

	private Collection<FormResource> resources = Generics.newLinkedList();
	
	protected void addScriptResource(String src) {
		addResource(new ScriptResource(src));
	}
	
	protected void addStylesheetResource(String src) {
		addResource(new StylesheetResource(src));
	}
	
	protected void addResource(FormResource resource) {
		resources.add(resource);
	}
	
	public Collection<FormResource> getResources() {
		return resources;
	}
}
