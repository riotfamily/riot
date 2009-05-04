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
package org.riotfamily.pages.page;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.component.preview.DefaultViewModeResolver;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.page.support.PageUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class PageHandlerMapping extends AbstractHandlerMapping {

	private static Log log = LogFactory.getLog(PageHandlerMapping.class);
	
	private PageMap pageMap;
	
	private ViewModeResolver viewModeResolver = new DefaultViewModeResolver();
	
	public PageHandlerMapping(PageMap map) {
		this.pageMap = map;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	protected PageMap getPageMap() {
		return this.pageMap;
	}

	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		String path = ServletUtils.getPathWithoutServletMapping(request);
		if (log.isDebugEnabled()) {
			log.debug("Looking up handler for [" + path + "]");
		}
		if (!pageMap.isInitialized()) {
			log.warn("PageMap not initialized ...");
			pageMap.initMappings();
			log.info("... pageMap built");
		}
		PageAndController pc = pageMap.getPageAndController(path);
		
		if (pc != null) {
			Page page = pc.getPage();
			if (page.isPublished() || (viewModeResolver != null 
				&& viewModeResolver.isPreviewMode(request))) {
				
				PageUtils.exposePage(request, page);
				PageUtils.exposePageMap(request, pageMap);
			
				return pc.getController();
			}
		}
		
		return null;
	}
}
