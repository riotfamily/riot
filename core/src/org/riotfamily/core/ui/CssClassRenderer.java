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
package org.riotfamily.core.ui;

import java.io.PrintWriter;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.ui.RenderContext;
import org.riotfamily.common.ui.StringRenderer;
import org.riotfamily.common.util.FormatUtils;
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
