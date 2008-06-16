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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.setup;

import java.util.HashMap;
import java.util.Map;

import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.setup.config.ChildHandlerNameDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class HandlerNameHierarchy implements ApplicationContextAware {

	private Map<String, String> childHandlerNames = new HashMap<String, String>();
	
	public void setApplicationContext(ApplicationContext ctx) {
		for (ChildHandlerNameDefinition def : 
				SpringUtils.beansOfTypeIncludingAncestors(ctx, 
				ChildHandlerNameDefinition.class).values()) {
		
			childHandlerNames.put(def.getParent(), def.getChild());
		}
	}
	
	public String getChildHandlerName(String parent) {
		return (String) childHandlerNames.get(parent);
	}
	
	public String getChildHandlerName(Page page) {
		String parent = page != null ? page.getHandlerName() : null;
		return (String) childHandlerNames.get(parent);
	}
	
	public String[] getChildHandlerNameOptions(Page page) {
		return StringUtils.commaDelimitedListToStringArray(getChildHandlerName(page));
	}
	
	public String initHandlerName(PageNode node) {
		String handlerName = null;
		if (node.getHandlerName() == null && node.getParent() != null) {
			handlerName = getChildHandlerName(node.getParent().getHandlerName());
			node.setHandlerName(handlerName);
		}
		return handlerName;
	}
}
