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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;

/**
 * Class that performs URL lookups for handlers mapped by a 
 * {@link ReverseHandlerMapping}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HandlerUrlResolver implements ApplicationContextAware {

	private List mappings;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		mappings = new ArrayList(applicationContext.getBeansOfType(
				ReverseHandlerMapping.class).values());
		
		if (!mappings.isEmpty()) {
			Collections.sort(mappings, new OrderComparator());
		}
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param prefix Optional prefix to sort out ambiguities
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request The current request
	 */
	public String getUrlForHandler(HttpServletRequest request, 
			String handlerName, Object attributes, String prefix) {
		
		Assert.notNull(mappings, "The ApplicationContext must be set first");
		String url = null;
		Iterator it = mappings.iterator();
		while (url == null && it.hasNext()) {
			ReverseHandlerMapping mapping = (ReverseHandlerMapping) it.next();
			url = mapping.getUrlForHandler(
					handlerName, prefix, attributes, request);
		}
		return url;
	}
}
