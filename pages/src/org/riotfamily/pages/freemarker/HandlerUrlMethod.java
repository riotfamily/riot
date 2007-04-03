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
package org.riotfamily.pages.freemarker;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.view.ViewContext;
import org.riotfamily.common.web.view.freemarker.AbstractSimpleMethod;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageLocationResolver;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HandlerUrlMethod extends AbstractSimpleMethod {

	private PageDao pageDao;
	
	private PageLocationResolver resolver;
	
	public HandlerUrlMethod(PageDao pageDao, PageLocationResolver resolver) {
		this.pageDao = pageDao;
		this.resolver = resolver;
	}

	protected Object exec(Object arg) throws Exception {
		String handlerName = (String) arg;
		HttpServletRequest request = ViewContext.getRequest();
		PageNode node = pageDao.getNodeForHandler(handlerName);
		if (node != null) {
			Locale locale = RequestContextUtils.getLocale(request);
			Page page = node.getPage(locale);
			if (page != null) {
				String url = resolver.getUrl(new PageLocation(page));
				return request.getContextPath() + url;
			}
		}
		return null;
	}
}
