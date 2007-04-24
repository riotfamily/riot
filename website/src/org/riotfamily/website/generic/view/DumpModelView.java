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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.generic.view;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.core.style.StylerUtils;
import org.springframework.web.servlet.View;

/**
 * View that dumps the content of the model as HTML page.
 * 
 * @see DumpModelViewResolver 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class DumpModelView implements View {

	public static final String DEFAULT_STYLE_SHEET = 
			"http://www.riotfamily.org/downloads/dump-model.css";
	
	private String styleSheet = DEFAULT_STYLE_SHEET;
	
	/**
	 * Sets the URL of a CSS style sheet that should be used to style the 
	 * output. Default is http://www.riotfamily.org/downloads/dump-model.css.
	 * If the URL is not external, the context-path is added automatically.
	 * 
	 * @param styleSheet URL to use, or <code>null</code> for an unformatted output.
	 */
	public void setStyleSheet(String styleSheet) {
		this.styleSheet = styleSheet;
	}

	public String getContentType() {
		return "text/html";
	}
	
	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		DocumentWriter out = new DocumentWriter(response.getWriter());
		out.start(Html.HTML);
		
		out.start(Html.HEAD);
		out.start(Html.TITLE).body("Model Dump").end();
		if (styleSheet != null) {
			out.start(Html.LINK).attribute(Html.LINK_REL, "stylesheet")
					.attribute(Html.LINK_HREF, 
					ServletUtils.resolveUrl(styleSheet, request)).end();
		}
		out.end();
		
		out.start(Html.BODY).start(Html.DL);
		Iterator it = model.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			out.start(Html.DT).body(entry.getKey().toString()).end();
			out.start(Html.DD).body(StylerUtils.style(entry.getValue())).end();
		}
		out.closeAll();
	}
}
