package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.web.util.ServletUtils;

public class LinkRenderer implements CellRenderer {

	private String property;
	
	private String prefix;
	
	private String suffix;
	
	private String target;
	
	private String messageKey;
	
	private String titleMessageKey;
	
	public void setProperty(String property) {
		this.property = property;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}	

	public void setTitleMessageKey(String titleMessageKey) {
		this.titleMessageKey = titleMessageKey;
	}

	public void render(RenderContext context, PrintWriter writer) {
		Object item = context.getItem(); 
		if (item != null) {
			StringBuffer url = new StringBuffer();
			if (prefix != null) {
				url.append(prefix);
			}
			if (property != null) {
				url.append(PropertyUtils.getProperty(item, property));
			}
			else {
				url.append(item);
			}
			if (suffix != null) {
				url.append(suffix);
			}
			String href = url.toString();
			if (!ServletUtils.isAbsoluteUrl(href) && href.startsWith("/")) {
				href = context.getContextPath() + href;
			}
			TagWriter tag = new TagWriter(writer);
			tag.start(Html.A);
			
			tag.attribute(Html.A_HREF, context.encodeURL(href));
			if (target != null) {
				tag.attribute(Html.A_TARGET, target);
			}
			if (titleMessageKey != null) {
				tag.attribute(Html.TITLE, context.getMessageResolver().getMessage(titleMessageKey));
			}
			if (messageKey != null) {
				tag.body(context.getMessageResolver().getMessage(messageKey));
			}
			tag.end();
		}
	}
}
