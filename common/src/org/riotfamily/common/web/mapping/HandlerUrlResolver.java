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
package org.riotfamily.common.web.mapping;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * Class that performs URL lookups for handlers mapped by a 
 * {@link ReverseHandlerMapping}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HandlerUrlResolver implements ApplicationContextAware {

	private List<ReverseHandlerMapping> mappings;
	
	private HandlerUrlResolver parent;
	
	private ApplicationContext applicationContext;
	
	
	public void setParent(HandlerUrlResolver parent) {
		this.parent = parent;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	private List<ReverseHandlerMapping> getMappings() {
		if (mappings == null) {
			Assert.notNull(applicationContext, "The ApplicationContext must be set first");
			mappings = SpringUtils.orderedBeans(applicationContext, 
					ReverseHandlerMapping.class);
		}
		return mappings;
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request The current request
	 */
	public String getUrlForHandler(HttpServletRequest request, 
			String handlerName, Object... attributes) {
		
		Object attr;
		if (attributes.length == 0) {
			attr = null;
		}
		else if (attributes.length == 1) {
			attr = attributes[0];
		}
		else {
			attr = attributes;
		}
		for (ReverseHandlerMapping mapping : getMappings()) {
			String url = mapping.getUrlForHandler(handlerName, attr, request);
			if (url != null) {
				return url;
			}
		}
		if (parent != null) {
			return parent.getUrlForHandler(request, handlerName, attributes);
		}
		return null;
	}
	
}
