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
package org.riotfamily.riot.list.command.core;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.list.command.CommandContext;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class LinkCommand extends PopupCommand {

	public static final String STYLE_CLASS = "link";
	
	private String link;
	
	private boolean contextRelative = true;
		
	public void setLink(String link) {
		this.link = link.replace('@', '$');
	}

	public void setContextRelative(boolean contextRelative) {
		this.contextRelative = contextRelative;
	}
	
	protected String getUrl(CommandContext context) {
		String url = PropertyUtils.evaluate(link, context.getBean());
		if (contextRelative && !ServletUtils.isAbsoluteUrl(url)) {
			url = context.getRequest().getContextPath() + url;
		}
		return url;
	}
	
	protected String getStyleClass(CommandContext context, String action) {
		return STYLE_CLASS;
	}

}
