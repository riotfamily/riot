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


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.riotfamily.common.util.FormatUtils;
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

	static Map<String, Object> defaults = Generics.newHashMap();
	static {
		defaults.put("skin", "riot");
		defaults.put("theme", "advanced");
		defaults.put("entity_encoding", "raw");
		defaults.put("valid_elements", "+a[href|target|name],-strong/b,-em/i,h3/h2/h1,h4/h5/h6,p,br,hr,ul,ol,li,blockquote");
		defaults.put("theme_advanced_containers", "buttons1,mceeditor");
		defaults.put("theme_advanced_container_buttons1", "formatselect,bold,italic,sup,bullist,numlist,outdent,indent,hr,link,unlink,anchor,code,undo,redo,charmap");
		defaults.put("theme_advanced_blockformats", "p,h3,h4");
	}
	
	private int rows = 10;

	private Map<String, Object> config;
	
	private String initScript;
	
	public TinyMCE() {
		setStyleClass("richtext");
		setWrap(false);
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public void setConfig(Map<String, Object> config) throws IOException {
		this.config = config;
	}
	
	@Override
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
		return new ScriptResource("tiny_mce/tiny_mce_src.js", "tinymce.WindowManager",
				new ScriptResource("tiny_mce/lazy_load_fix.js", "tinyMCE_GZ"));
	}

	private String getJsonConfig() {
		Map<String, Object> merged = Generics.newHashMap();
		merged.putAll(defaults);
		if (config != null) {
			merged.putAll(config);
		}
		merged.put("mode", "exact");
		merged.put("elements", getId());
		merged.put("language", getFormContext().getLocale().getLanguage().toLowerCase());
		merged.put("add_unload_trigger", false);
		merged.put("submit_patch", false);
		merged.put("strict_loading_mode", true);
		merged.put("relative_urls", false);
		merged.put("theme_advanced_layout_manager", "RowLayout");
		merged.put("theme_advanced_containers_default_align", "left");
		merged.put("theme_advanced_container_mceeditor", "mceeditor");
		return FormatUtils.toJSON(merged);
		/*
		json.element("setupcontent_callback", 
				new JSONFunction(new String[] {"id", "body", "doc"},
				"if (window.registerKeyHandler) registerKeyHandler(doc);"));
		
		json.element("save_callback", 
				new JSONFunction(new String[] {"id", "html", "body"},
				"return html.replace(/<!--(.|\\n)*?-->/g, '')" 
				+ ".replace(/&lt;!--(.|\\n)*?\\smso-(.|\\n)*?--&gt;/g, '');"));
		
		return json.toString();
		*/
	}
	
	public String getInitScript() {
		if (!isEnabled()) {
			return null;
		}
		if (initScript == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("tinymce.dom.Event._pageInit();");
			sb.append("tinyMCE.init(").append(getJsonConfig()).append(");");
			initScript = sb.toString();
		}
		return initScript;
	}

}
