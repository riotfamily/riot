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


import java.io.PrintWriter;
import java.util.Map;

import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.TagWriter;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;


/**
 * A WYSIWYG richtext editor based on TinyMCE.
 */
public class TinyMCE extends AbstractTextElement
		implements ResourceElement, DHTMLElement {

	static Map<String, String> defaults = Generics.newHashMap();
	static {
		defaults.put("entity_encoding", "raw");
		defaults.put("valid_elements", "+a[href|target|name],strong,em,h3/h2/h1,h4/h5/h6,p,br,hr,ul,ol,li,blockquote,sub,sup,span[class&lt;mailto]");
	}
	
	private int rows = 10;

	private Map<String, ?> config;
	
	private String initScript;
	
	public TinyMCE() {
		setStyleClass("richtext");
		setWrap(false);
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public void setConfig(Map<String, ?> config) {
		this.config = config;
	}
	
	public void renderInternal(PrintWriter writer) {
		TagWriter tag = new TagWriter(writer);
		if (isEnabled()) {
			tag.start("textarea")
				.attribute("id", getId())
				.attribute("class", getStyleClass())			
				.attribute("name", getParamName())
				.attribute("rows", rows)
				.body(getText()).end();
		}
		else {
			tag.start("div")
				.attribute("id", getId())
				.attribute("class", "tinymce-disabled")				
				.body(getText(), false).end();
		}
	}

	public FormResource getResource() {
		return new ScriptResource("tinymce/tinymce.min.js", "tinymce.WindowManager");
	}

	private String getJsonConfig() {
		JSONObject json = JSONObject.fromObject(defaults);
		if (config != null) {
			json.putAll(config);
		}
		json.element("selector", "textarea#" + getId());
		json.element("language", getFormContext().getLocale().getLanguage().toLowerCase());
		json.element("relative_urls", Boolean.FALSE);

		JSONFunction beforeSetContent = 
				new JSONFunction(new String[] {"e"}, "if (window.registerKeyHandler) registerKeyHandler(e.target.getDoc());");
		
		JSONFunction saveCallback = 
				new JSONFunction(new String[] {"e"}, "var ed = e.target;" + 
					"e.content = e.content.replace(/<!--(.|\\n)*?-->/g, '').replace(/&lt;!--(.|\\n)*?\\smso-(.|\\n)*?--&gt;/g, '');");
		
		StringBuilder setup = new StringBuilder();
		setup.append("ed.on('BeforeSetContent',").append(beforeSetContent.toString()).append(");");
		setup.append("ed.on('SaveContent', ").append(saveCallback.toString()).append(");");
		if (hasListeners()) {
			
			JSONFunction fireChangeEvent = 
					new JSONFunction(new String[] {"e"}, "var source = $('"+getId()+"');"+
							"var ed = e.target;" +
							"source.value = ed.getContent();" + 
							"submitEvent(new ChangeEvent(source));");
			
			setup.append("ed.on('change', ").append(fireChangeEvent.toString()).append(");");
		}
		
		json.element("setup", 
				new JSONFunction(new String[] {"ed"}, setup.toString()));
		
		return json.toString();
	}
	
	public String getInitScript() {
		if (!isEnabled()) {
			return null;
		}
		if (initScript == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("tinyMCE.init(").append(getJsonConfig()).append(");");
			
			sb.append("if (!window.beforeListMove) window.beforeListMove = function() { "
					+ "window.movedTinyMCE = []; "
					+"for (var i = 0; i < window.tinyMCE.editors.length; i++) { window.movedTinyMCE.push(window.tinyMCE.editors[i].id); }"
					+"for (var i = 0; i < window.movedTinyMCE.length; i++) { window.tinyMCE.EditorManager.execCommand('mceRemoveEditor',true, window.movedTinyMCE[i]); } "
					+ "}; ");
			
			sb.append("if (!window.afterListMove) window.afterListMove = function() { "
					+"for (var i = 0; i < window.movedTinyMCE.length; i++) { window.tinyMCE.EditorManager.execCommand('mceAddEditor',true, window.movedTinyMCE[i]); } "
					+ "}; ");
			initScript = sb.toString();
		}
		return initScript;
	}

}
