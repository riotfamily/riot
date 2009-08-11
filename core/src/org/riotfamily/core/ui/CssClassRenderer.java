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
