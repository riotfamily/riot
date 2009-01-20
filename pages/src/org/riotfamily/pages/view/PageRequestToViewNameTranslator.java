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
package org.riotfamily.pages.view;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.springframework.web.servlet.RequestToViewNameTranslator;

/**
 * RequestToViewNameTranslator that uses the pageType of the resolved Page
 * to construct a viewName.
 * 
 * @see PageResolver
 * @since 8.0
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PageRequestToViewNameTranslator 
		implements RequestToViewNameTranslator {

	private String prefix = "";
	
	private String suffix = "";
	
	private String defaultPageType = "default";
	
	private RequestToViewNameTranslator noPageTranslator;
	
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setDefaultPageType(String defaultPageType) {
		this.defaultPageType = defaultPageType;
	}
	
	public void setNoPageTranslator(RequestToViewNameTranslator noPageTranslator) {
		this.noPageTranslator = noPageTranslator;
	}

	public String getViewName(HttpServletRequest request) throws Exception {
		Page page = PageResolver.getResolvedPage(request);
		if (page != null) {
			String pageType = page.getPageType();
			if (pageType == null) {
				pageType = defaultPageType;
			}
			return prefix + pageType + suffix;
		}
		else if (noPageTranslator != null) {
			return noPageTranslator.getViewName(request);
		}
		return null;
	}

}
