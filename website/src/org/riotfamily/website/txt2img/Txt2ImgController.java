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

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author flx
 * @since 6.5
 */
public class Txt2ImgController extends AbstractCacheableController {

	private long lastModified = System.currentTimeMillis();
	
	private Map generators;
	
	private ImageGenerator defaultGenerator = new ImageGenerator();
	
	private Pattern refererPattern;
	
	public void setGenerators(Map generators) {
		this.generators = generators;
	}
	
	public void setDefaultGenerator(ImageGenerator defaultGenerator) {
		this.defaultGenerator = defaultGenerator;
	}

	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		key.append(request.getQueryString());
	}
	
	public long getLastModified(HttpServletRequest request) {
        return lastModified;
    }
	
	public void setRefererPattern(Pattern refererPattern) {
		this.refererPattern = refererPattern;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
	
		if (refererPattern != null) {
			String referer = request.getHeader("Referer");
			if (referer != null && !refererPattern.matcher(referer).matches()) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		
		ImageGenerator generator = defaultGenerator;
		String selector = request.getParameter("selector");
		if (selector != null) {
			generator = (ImageGenerator) generators.get(selector);
		}
		Assert.notNull(generator, "No ImageGenerator found for selector '"
				+ selector + " and no default generator is set.");
		
		String text = FormatUtils.stripWhitespaces(request.getParameter("text"), true);
		int width = ServletRequestUtils.getIntParameter(request, "width", 800);
		if (width <= 0) {
			width = 800;
		}
		String color = request.getParameter("color");
		response.setContentType("image/png");
		generator.generate(text, width, color, response.getOutputStream());
		return null;
	}
}
