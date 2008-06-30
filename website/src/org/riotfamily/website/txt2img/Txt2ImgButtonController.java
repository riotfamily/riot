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

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class Txt2ImgButtonController implements Controller {

	Txt2ImgMacroHelperFactory factory;
	
	public Txt2ImgButtonController(Txt2ImgMacroHelperFactory factory) {
		this.factory = factory;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String style = (String) request.getAttribute("style");
		if (style == null) {
			File f = new File(factory.getButtonDir(), "buttons.css");
			response.setContentType("text/css");
			IOUtils.serve(f, response.getOutputStream());	
		}
		else {
			File dir = new File(factory.getButtonDir(), style);
			String fileName = (String) request.getAttribute("fileName");
			File f = new File(dir, FormatUtils.sanitizePath(fileName).replace(' ', '+'));
			IOUtils.serve(f, response.getOutputStream());
		}
		return null;
	}

}
