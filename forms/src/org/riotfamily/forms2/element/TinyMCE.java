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
package org.riotfamily.forms2.element;

import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.client.Html;
import org.springframework.context.i18n.LocaleContextHolder;

public class TinyMCE extends TextArea {

	private static Map<String, Object> defaults = Generics.newHashMap();
	static {
		defaults.put("skin", "riot");
		defaults.put("theme", "advanced");
		defaults.put("entity_encoding", "raw");
		defaults.put("valid_elements", "+a[href|target|name],-strong/b,-em/i,h3/h2/h1,h4/h5/h6,p,br,hr,ul,ol,li,blockquote");
		defaults.put("theme_advanced_containers", "buttons1,mceeditor");
		defaults.put("theme_advanced_container_buttons1", "formatselect,bold,italic,sup,bullist,numlist,outdent,indent,hr,link,unlink,anchor,code,undo,redo,charmap");
		defaults.put("theme_advanced_blockformats", "p,h3,h4");
	}
	
	private Map<String, Object> config;
		
	public static class State<T extends TinyMCE> extends TextArea.State<T> {
		
		@Override
		protected void renderInternal(Html html, T mce) {
			super.renderInternal(html, mce);
			Locale locale = LocaleContextHolder.getLocale();
			html.script("tinyMCE.init(%s)", getJsonConfig(mce, locale));
		}
		
		protected String getJsonConfig(T mce, Locale locale) {
			Map<String, Object> merged = Generics.newHashMap();
			merged.putAll(defaults);
			if (mce.config != null) {
				merged.putAll(mce.config);
			}
			merged.put("mode", "exact");
			merged.put("elements", getId());
			merged.put("language", locale.getLanguage().toLowerCase());
			merged.put("add_unload_trigger", false);
			merged.put("submit_patch", false);
			merged.put("strict_loading_mode", true);
			merged.put("relative_urls", false);
			merged.put("theme_advanced_layout_manager", "RowLayout");
			merged.put("theme_advanced_containers_default_align", "left");
			merged.put("theme_advanced_container_mceeditor", "mceeditor");
			return FormatUtils.toJSON(merged);
		}
	}
	
	
}
