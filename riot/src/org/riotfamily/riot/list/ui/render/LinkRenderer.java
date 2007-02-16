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
package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.list.command.core.LinkCommand;

/**
 * @deprecated Please use the {@link LinkCommand} instead.
 */
public class LinkRenderer implements CellRenderer {

	private String prefix;
	
	private String suffix;
	
	private String target;
	
	private String messageKey;
	
	private String titleMessageKey;
	
	/**
	 * @deprecated No longer used.
	 */
	public void setProperty(String property) {
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
	
	public void render(String propertyName, Object item, RenderContext context, 
			PrintWriter writer) {
		
		if (item != null) {
			StringBuffer url = new StringBuffer();
			if (prefix != null) {
				url.append(prefix);
			}
			if (propertyName != null) {
				url.append(PropertyUtils.getProperty(item, propertyName));
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
			
			tag.attribute(Html.A_HREF, href);
			if (target != null) {
				tag.attribute(Html.A_TARGET, target);
			}
			if (titleMessageKey != null) {
				tag.attribute(Html.TITLE, context.getMessageResolver()
						.getMessage(titleMessageKey));
			}
			if (messageKey != null) {
				tag.body(context.getMessageResolver().getMessage(messageKey));
			}
			tag.end();
		}
	}
}
