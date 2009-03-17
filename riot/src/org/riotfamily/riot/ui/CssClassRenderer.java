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
package org.riotfamily.riot.ui;

import java.io.PrintWriter;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.ui.RenderContext;
import org.riotfamily.common.web.ui.StringRenderer;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

public class CssClassRenderer extends StringRenderer {
	
	private String labelMessageKey;
	
	private boolean appendLabel;
	
	public String getLabelMessageKey() {
		return labelMessageKey;
	}
	
	public void setLabelMessageKey(String labelMessageKey) {
		this.labelMessageKey = labelMessageKey;
	}
	
	public boolean isAppendLabel() {
		return appendLabel;
	}
	
	public void setAppendLabel(boolean appendLabel) {
		this.appendLabel = appendLabel;
	}

	protected void renderString(String string, RenderContext context, 
			PrintWriter writer) {
		
		writer.print("<div class=\"css-cell ");
		writer.print(FormatUtils.toCssClass(string));
		
		if (getLabelMessageKey() != null) {
			MessageResolver messageResolver = context.getMessageResolver();
			String label;
			if (isAppendLabel()) {
				label = messageResolver.getMessage(getLabelMessageKey() + string, null,
						FormatUtils.fileNameToTitleCase(string));
			}
			else {
				label = messageResolver.getMessage(getLabelMessageKey() , new Object[] {string},
						FormatUtils.fileNameToTitleCase(string));
			}
			if (StringUtils.hasText(label)) {
				writer.print("\" title=\"");
				writer.print(HtmlUtils.htmlEscape(label));
			}
		}
		
		writer.print("\"></div>");
	}

}
