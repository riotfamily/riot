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
package org.riotfamily.forms2.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * ResourceVisitor that generates JavaScript to load required resources.
 */
public class LoadingCodeGenerator implements ResourceVisitor {

	private LinkedHashSet<ScriptResource> scripts = new LinkedHashSet<ScriptResource>();
	
	private LinkedHashSet<StylesheetResource> stylesheets = new LinkedHashSet<StylesheetResource>();

	private LoadingCodeGenerator() {
	}
	
	public static String generate(Collection<FormResource> resources) {
		StringBuilder sb = new StringBuilder();
		new LoadingCodeGenerator().render(resources, sb);
		return sb.toString();
	}
	
	private void loadResources(Collection<FormResource> resources) {
		if (resources == null) {
			return;
		}
		for (FormResource resource : resources) {
			if (resource != null) {
				resource.accept(this);
			}
		}
	}
	
	public void visitScript(ScriptResource script) {
		if (!scripts.contains(script)) {
			loadResources(script.getDependencies());
			scripts.add(script);
		}
	}

	public void visitStyleSheet(StylesheetResource stylesheet) {
		if (!stylesheets.contains(stylesheet)) {
			stylesheets.add(stylesheet);
		}
	}

	private void render(Collection<FormResource> resources, StringBuilder sb) {
		loadResources(resources);
		for (StylesheetResource stylesheet : stylesheets) {
			sb.append("riot.Resources.loadStyleSheet('");
			sb.append(stylesheet.getUrl());
			sb.append("');");
		}
		
		if (!scripts.isEmpty()) {
			sb.append("riot.Resources.loadScriptSequence([");
			Iterator<ScriptResource> it = scripts.iterator();
			while (it.hasNext()) {
				ScriptResource script = it.next();
				sb.append("{src:'");
				sb.append(script.getUrl());
				sb.append('\'');
				if (script.getTest() != null) {
					sb.append(", test:'");
					sb.append(script.getTest());
					sb.append('\'');
				}
				sb.append("}");
				if (it.hasNext()) {
					sb.append(',');
				}
			}
			sb.append("]);");
		}
	}
	
}
