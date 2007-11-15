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
package org.riotfamily.common.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mapping.HandlerUrlResolver;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

/**
 * View that sends a redirect to a named handler.
 * 
 * @see HandlerUrlResolver
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class NamedHandlerRedirectView extends RedirectView {

	private HandlerUrlResolver handlerUrlResolver;
	
	private String handlerName;
	
	public NamedHandlerRedirectView(String handlerName) {
		this(handlerName, null);
	}
	
	public NamedHandlerRedirectView(String handlerName, 
			HandlerUrlResolver handlerUrlResolver) {
		
		this.handlerName = handlerName;
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (handlerUrlResolver == null) {
			WebApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
			handlerUrlResolver = (HandlerUrlResolver) context.getBean("handlerUrlResolver");
		}
		String handlerUrl = handlerUrlResolver.getUrlForHandler(request, handlerName, model, null);
		Assert.notNull(handlerUrl, "Can't resolve URL for handler " + handlerName);
		setUrl(handlerUrl);
		setContextRelative(true);
		super.render(model, request, response);
	}

}
