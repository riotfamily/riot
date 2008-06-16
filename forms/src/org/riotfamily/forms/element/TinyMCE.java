/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element;


import java.io.PrintWriter;
import java.util.Map;

import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.Generics;
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
		defaults.put("skin", "riot");
		defaults.put("theme", "advanced");
		defaults.put("entity_encoding", "raw");
		defaults.put("valid_elements", "+a[href|target|name],-strong/b,-em/i,h3/h2/h1,h4/h5/h6,p,br,hr,ul,ol,li,blockquote");
		defaults.put("theme_advanced_containers", "buttons1,mceeditor");
		defaults.put("theme_advanced_container_buttons1", "formatselect,bold,italic,sup,bullist,numlist,outdent,indent,hr,link,unlink,anchor,code,undo,redo,charmap");
		defaults.put("theme_advanced_blockformats", "p,h3,h4");
	}
	
	private int rows = 10;

	private Map<String, ?> config;
	
	private String initScript;
	
	public TinyMCE() {
		setStyleClass("richtext");
		setSurroundByDiv(false);
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
			tag.start(Html.TEXTAREA)
				.attribute(Html.COMMON_ID, getId())
				.attribute(Html.COMMON_CLASS, getStyleClass())			
				.attribute(Html.INPUT_NAME, getParamName())
				.attribute(Html.TEXTAREA_ROWS, rows)
				.body(getText()).end();
		}
		else {
			tag.start(Html.DIV)
				.attribute(Html.COMMON_ID, getId())
				.attribute(Html.COMMON_CLASS, "tinymce-disabled")				
				.body(getText(), false).end();
		}
	}

	public FormResource getResource() {
		return new ScriptResource("tiny_mce/tiny_mce_src.js", "tinymce.WindowManager",
				new ScriptResource("tiny_mce/lazy_load_fix.js", "tinyMCE_GZ"));
	}

	private String getJsonConfig() {
		JSONObject json = JSONObject.fromObject(defaults);
		if (config != null) {
			json.putAll(config);
		}
		json.element("mode", "exact");
		json.element("elements", getId());
		json.element("language", getFormContext().getLocale().getLanguage().toLowerCase());
		json.element("add_unload_trigger", false);
		json.element("submit_patch", false);
		json.element("strict_loading_mode", true);
		json.element("relative_urls", Boolean.FALSE);
		json.element("theme_advanced_layout_manager", "RowLayout");
		json.element("theme_advanced_containers_default_align", "left");
		json.element("theme_advanced_container_mceeditor", "mceeditor");
		json.element("setupcontent_callback", 
				new JSONFunction(new String[] {"id", "body", "doc"},
				"if (window.registerKeyHandler) registerKeyHandler(doc);"));
		
		return json.toString();
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
