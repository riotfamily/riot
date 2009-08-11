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
package org.riotfamily.components.render.component;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.freemarker.RiotFreeMarkerView;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.components.meta.ComponentMetaData;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.meta.FreeMarkerMetaDataExtractor;
import org.springframework.web.servlet.View;

import freemarker.template.Template;

public class ViewComponentMetaDataProvider implements ComponentMetaDataProvider {

	private RiotLog log = RiotLog.get(this);
	
	private ViewComponentRenderer renderer;
	
	public ViewComponentMetaDataProvider(ViewComponentRenderer renderer) {
		this.renderer = renderer;
	}

	public ComponentMetaData getMetaData(String type) {
		View view = renderer.getView(type);
		Map<String, String> data = null;
		if (view instanceof RiotFreeMarkerView) {
			try {
				Template template = ((RiotFreeMarkerView) view).getTemplate(Locale.getDefault());
				data = FreeMarkerMetaDataExtractor.extractMetaData(template);
			} 
			catch (IOException e) {
				log.warn("Failed to extract component meta data", e);
			}
		}
		return new ComponentMetaData(type, data);
	}
}
