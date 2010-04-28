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

import org.riotfamily.common.util.FormatUtils;

/**
 * ResourceVisitor that generates JavaScript to load required resources.
 */
public class LoadingCodeGenerator implements ResourceVisitor {

	private LinkedHashSet<ScriptResource> scripts = new LinkedHashSet<ScriptResource>();
	
	private LinkedHashSet<StylesheetResource> stylesheets = new LinkedHashSet<StylesheetResource>();

	private String resourcePath;
	
	public static void addLoadingCode(Collection<FormResource> resources, String resourcePath, Html html) {
		new LoadingCodeGenerator(resources, resourcePath).addLoadingCode(html);
	}
	
	private LoadingCodeGenerator(Collection<FormResource> resources, String resourcePath) {
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

	private void addLoadingCode(Html html) {
		String initializationCode = html.extractScripts();
		html.script(jQuery()).inlineScripts()
			.script(loadResources(initializationCode)).inlineScripts();
	}
	
	private String jQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("if (!window.jQuery) {"); 
		sb.append("document.write('<script src=\"").append(resourcePath).append("jquery/jquery.js").append("\"><\\/script>');");
		sb.append("}");
		return sb.toString();
	}
	
	private String loadResources(String initCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("(function($) {");
		
		// The base path for all resources
		sb.append("var base='").append(resourcePath).append("';");
		
		// Load style sheets
		sb.append("$.each(");
		sb.append(FormatUtils.toJSON(stylesheets));
		sb.append(", function() { $('<link>', {rel:  \"stylesheet\", type: \"text/css\", href: base + this}).appendTo('head');});");
		
		// Utility function to check whether an object-path expression is defined
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
			  );
		
		// Array containing all scripts to load
		sb.append("var scripts=").append(FormatUtils.toJSON(scripts)).append(";");	  
		
		sb.append("function loadScripts() {"
			  +     "if (scripts.length > 0) {"
			  +       "var s = scripts.shift();"
			  +       "if (!s.test || !isPresent(s.test)) {"
			  +         "$.getScript(base + s.url, loadScripts);"
			  +         "return;"
			  +       "}"
			  +       "loadScripts();"
			  +     "}"
			  +     "else {"
			  +       "initForm()"
			  +     "}"
			  +   "}"	
			  +   "loadScripts();"
			  );
		
		/*
		// Alternative method: Allows debugging but does not work when loaded lazily
		sb.append("function loadScripts() {"
			  +     "$.each(scripts, function() {"
			  +       "if (!this.test || !isPresent(this.test)) {"
			  +         "document.write('<script src=\"' + base + this.url + '\"><\\/script>');"
			  +       "}"
			  +     "});"
			  +   "}"
			  +   "loadScripts();"
			  );
		*/
		
		// Callback function that is invoked after all resources have been loaded
		sb.append("function initForm() {");
		sb.append(initCode);
		sb.append("}");
		
		sb.append("})(jQuery);");
		
		return sb.toString();
	}
	
}
