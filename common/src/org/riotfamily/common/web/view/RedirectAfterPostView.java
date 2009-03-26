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

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.web.servlet.view.RedirectView;

/**
 * View that sends a redirect to the originating request URI.
 * <b>NOTE:</b> The implementation is not thread safe.
 *  
 * @see ServletUtils#getOriginatingRequestUri(HttpServletRequest)
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RedirectAfterPostView extends RedirectView {

	@SuppressWarnings("unchecked")
	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		setHttp10Compatible(ServletUtils.isHttp10(request));
		setUrl(ServletUtils.getOriginatingRequestUri(request));
		super.render(model, request, response);
	}
	
}
