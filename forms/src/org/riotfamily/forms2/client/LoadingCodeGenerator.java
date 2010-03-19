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
import java.util.LinkedHashSet;

/**
 * ResourceVisitor that generates JavaScript to load required resources.
 */
public class LoadingCodeGenerator implements ResourceVisitor {

	private LinkedHashSet<ScriptResource> scripts = new LinkedHashSet<ScriptResource>();
	
	private LinkedHashSet<StylesheetResource> stylesheets = new LinkedHashSet<StylesheetResource>();

	private String resourcePath;
	
	public LoadingCodeGenerator(Collection<FormResource> resources, String resourcePath) {
		this.resourcePath = resourcePath;
		process(resources);
	}
		
	private void process(Collection<FormResource> resources) {
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
			process(script.getDependencies());
			scripts.add(script);
		}
	}

	public void visitStyleSheet(StylesheetResource stylesheet) {
		if (!stylesheets.contains(stylesheet)) {
			stylesheets.add(stylesheet);
		}
	}

	public String scripts() {
		StringBuilder sb = new StringBuilder();
		sb.append("(function(){");
		sb.append("var base='").append(resourcePath).append("';");
		sb.append("function isPresent(exp) {"
			  +     "var s = exp.split('.');"
			  +     "exp = '';"
			  +     "for (var i = 0; i < s.length; i++) {"
			  +       "exp += s[i];"
			  +       "if (eval('typeof ' + exp) == 'undefined') return false;"
			  +       "exp += '.';"
			  +     "}"
			  +     "return true;"
			  +   "}"
			  +   "function load(src, test) {"
			  +     "if (!test || !isPresent(test)) document.write('<script src=\"' + base + src + '\"><\\/script>');"
			  +   "}");
			
		for (ScriptResource script : scripts) {
			sb.append("load('").append(script.getUrl()).append("'");
			if (script.getTest() != null) {
				sb.append(", '").append(script.getTest()).append("'");	
			}
			sb.append(");");
		}
		sb.append("})();");
		return sb.toString();
	}
	
	public String stylesheets() {
		StringBuilder sb = new StringBuilder();
		for (StylesheetResource stylesheet : stylesheets) {
			sb.append("riot.form.loadStyleSheet('");
			sb.append(resourcePath);
			sb.append(stylesheet.getUrl());
			sb.append("');");
		}
		return sb.toString();
	}
	
}
