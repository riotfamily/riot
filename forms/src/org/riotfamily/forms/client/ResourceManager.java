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

import org.riotfamily.common.util.FormatUtils;

public class ResourceManager implements Serializable {
	
	private static Resources requiredResources = new Resources()
			.stylesheet("forms/form.css")
			.stylesheet("jquery/ui/jquery-ui.css")
			.script("jquery/ui/jquery-ui.js");
	
	private String contextPath = "";
	
	private String resourcePath = "/resources/";
	
	private Resources resources;

	private String jQueryJs = "jquery/jquery.js";
	private String formJs = "forms/form.js";
	
	
	public ResourceManager setContextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}
	
	public ResourceManager setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
		return this;
	}
	
	public String resolveResource(String res) {
		return contextPath + resourcePath + res;
	}

	public void init(Resources resources, Html html) {
		this.resources = new Resources(requiredResources).add(resources);
		html.script(loadJQuery()).embedScripts();
		html.script(loadStylesheets()).embedScripts();
	}
	
	public void addLoadingCode(Html html) {
		String callback = html.extractScripts();
		html.script(loadScripts(callback)).embedScripts();
	}
		
	private String loadJQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("if (!window.jQuery) {"); 
		sb.append("document.write('<script src=\"").append(resolveResource(jQueryJs)).append("\"><\\/script>');");
		sb.append("}");
		return sb.toString();
	}
	
	private String loadStylesheets() {
		return String.format("jQuery.each(%s, function() { " 
				+   "$('<link>', {rel: 'stylesheet', type: 'text/css', href: '%s' + this}).appendTo('head');" 
				+ "});", 
				FormatUtils.toJSON(resources.getStylesheets()), 
				resolveResource(""));
	}
	
	private String loadScripts(String callback) {
		return String.format("jQuery.getScript('%s', function() {"
				+ "riot.form.setResourceBaseUrl('%s');"
				+ "riot.form.loadScripts(%s, function() {%s});"
				+ "});", 
				resolveResource(formJs),
				resolveResource(""),
				FormatUtils.toJSON(resources.getScripts()),
				callback);
	}
	
}
