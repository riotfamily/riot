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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.txt2img;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.cachius.spring.Compressible;
import org.springframework.web.servlet.ModelAndView;

public class ButtonStylesheetController implements CacheableController, Compressible {

	private ButtonService buttonService;
	
	public ButtonStylesheetController(ButtonService buttonService) {
		this.buttonService = buttonService;
	}

	public boolean gzipResponse(HttpServletRequest request) {
		return true;
	}
	
	public String getCacheKey(HttpServletRequest request) {
		return "txt2imgButtonRules";
	}

	public long getTimeToLive() {
		return buttonService.isReloadable() ? 0 : CACHE_ETERNALLY;
	}
	
	public long getLastModified(HttpServletRequest request) throws Exception {
		return buttonService.getLastModified();
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/css");
		buttonService.writeRules(response.getWriter());
		return null;
	}

}
