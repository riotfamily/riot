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
package org.riotfamily.website.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.DeferredRenderingResponseWrapper;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 * TemplateController that allows slots to be processed before others while
 * still being rendered at the original position within the template.
 * <p>
 * These 'pushed-up' controllers can expose request attributes which can then
 * be read by other controllers that would usually be processed before.
 * A common usecase is when a controller that renders the page content also
 * wants to control the document's title tag.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PushUpTemplateController extends TemplateController {

	private static final Log log = LogFactory.getLog(
			PushUpTemplateController.class);

	private static final String RESPONSE_WRAPPERS_ATTRIBUTE =
			PushUpTemplateController.class.getName() + ".responseWrappers";

	protected static final String SLOT_TO_RENDER_PARAMETER = "slotToRender";

	private List pushUpSlots;

	public void setPushUpSlots(List pushUpSlots) {
		this.pushUpSlots = pushUpSlots;
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String slotToRender = request.getParameter(SLOT_TO_RENDER_PARAMETER);
		if (slotToRender != null) {
			renderCapturedSlot(request, response, slotToRender);
			return null;
		}
		if (handlePushUps(request, response)) {
			return null;
		}
		return super.handleRequestInternal(request, response);
	}

	/**
	 * Handles the push-up slots. Returns <code>true</code> if one of the 
	 * controllers sent an error or redirect.
	 */
	protected boolean handlePushUps(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		if (pushUpSlots != null && !pushUpSlots.isEmpty()) {
			//REVISIT Perhaps we should a check whether this is a top-level request ...
			HashMap config = new HashMap();
			request.setAttribute(SLOTS_CONFIGURATION_ATTRIBUTE, config);
			Iterator it = pushUpSlots.iterator();
			while (it.hasNext()) {
				String slot = (String) it.next();
				config.put(slot, getDeferredRenderingUrl(request, slot));
				if (handlePushUp(request, response, slot)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns an URL that can be requested in order to render a captured
	 * slot. The URL is constructed from the current servletPath by appending
	 * a parameter that contains the given slot path.
	 */
	protected String getDeferredRenderingUrl(HttpServletRequest request,
			String slot) {

		StringBuffer url = new StringBuffer()
				.append(ServletUtils.getPathWithinApplication(request))
				.append('?').append(SLOT_TO_RENDER_PARAMETER).append('=')
				.append(slot);

		return url.toString();
	}

	protected final Map getResponseWrappers(HttpServletRequest request) {
		Map wrappers = (Map) request.getAttribute(RESPONSE_WRAPPERS_ATTRIBUTE);
		if (wrappers == null) {
			wrappers = new HashMap();
			request.setAttribute(RESPONSE_WRAPPERS_ATTRIBUTE, wrappers);
		}
		return wrappers;
	}

	protected boolean handlePushUp(HttpServletRequest request,
			HttpServletResponse response, String slot)
			throws ServletException, IOException {

		DeferredRenderingResponseWrapper responseWrapper =
			new DeferredRenderingResponseWrapper(response);

		getResponseWrappers(request).put(slot, responseWrapper);
		request.setAttribute(SLOT_PATH_ATTRIBUTE, slot);

		String url = (String) getMergedConfiguration().get(slot);

		log.debug("Capturing pushed-up slot " + slot + " [" + url + "]");
		request.getRequestDispatcher(url).forward(
				request, responseWrapper);
		
		return responseWrapper.isRedirectSent();
	}

	/**
	 * Renders a previously captured response for the given slot.
	 */
	protected void renderCapturedSlot(HttpServletRequest request,
			HttpServletResponse response, String slot) throws IOException {

		log.debug("Rendering captured slot: " + slot);
		DeferredRenderingResponseWrapper responseWrapper =
				(DeferredRenderingResponseWrapper)
				getResponseWrappers(request).get(slot);

		Assert.notNull(responseWrapper,
				"No wrapped response found for slot: " + slot);

		responseWrapper.renderResponse(response);
	}
}
