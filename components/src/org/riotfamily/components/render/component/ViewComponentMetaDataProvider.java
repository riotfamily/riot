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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.render.component;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.common.web.view.freemarker.RiotFreeMarkerView;
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
