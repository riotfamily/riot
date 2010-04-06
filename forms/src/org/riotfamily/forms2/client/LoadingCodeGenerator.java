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
		sb.append("var scripts=[");
		Iterator<ScriptResource> it = scripts.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("];");
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
			  +   "var useAjax = window.jQuery;"
			  +   "function loadScripts() {"
			  +     "if (scripts.length > 0) {"
			  +       "var s = scripts.shift();"
			  +       "if (!s.test || !isPresent(s.test)) {"
			  +         "if (useAjax) {"
			  +           "$.getScript(base + s.url, loadScripts);"
			  +           "return;"
			  +         "}"
			  +         "document.write('<script src=\"' + base + s.url + '\"><\\/script>');"
			  +       "}"
			  +       "loadScripts();"
			  +     "}"
			  +   "}"	
			  +   "loadScripts();");

		sb.append("})();");
		return sb.toString();
	}
	
	public String stylesheets() {
		StringBuilder sb = new StringBuilder();
		sb.append("$.each([");
		Iterator<StylesheetResource> it = stylesheets.iterator();
		while (it.hasNext()) {
			sb.append("'").append(resourcePath).append(it.next().getUrl()).append("'");
			if (it.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("], function() { $('<link>', {rel:  \"stylesheet\", type: \"text/css\", href: this}).appendTo('head');});");
		return sb.toString();
	}
	
}
