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
import org.riotfamily.pages.setup.config.ChildPageTypeDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PageTypeHierarchy implements ApplicationContextAware {

	private Map<String, String> childTypes = new HashMap<String, String>();
	
	public void setApplicationContext(ApplicationContext ctx) {
		for (ChildPageTypeDefinition def : 
				SpringUtils.beansOfTypeIncludingAncestors(ctx, 
				ChildPageTypeDefinition.class).values()) {
		
			childTypes.put(def.getParent(), def.getChild());
		}
	}
	
	public String getChildType(String parentType) {
		return childTypes.get(parentType);
	}
	
	public String getChildType(Page page) {
		String parent = page != null ? page.getPageType() : null;
		return childTypes.get(parent);
	}
	
	public String[] getChildTypeOptions(Page page) {
		return StringUtils.commaDelimitedListToStringArray(getChildType(page));
	}
	
	public String initPageType(Page page) {
		String pageType = null;
		if (page.getPageType() == null && page.getParentPage() != null) {
			pageType = getChildType(page.getParentPage().getPageType());
			page.setPageType(pageType);
		}
		return pageType;
	}
}
