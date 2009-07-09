package org.riotfamily.website.template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.DeferredRenderingResponseWrapper;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class RenderSlotController implements Controller {

	private static Log log = LogFactory.getLog(RenderSlotController.class);
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String slot = (String) request.getAttribute("slot");
		Assert.hasText(slot, "Missing parameter 'slot'");
		
		log.debug("Rendering captured slot: " + slot);
		
		DeferredRenderingResponseWrapper responseWrapper =
				(DeferredRenderingResponseWrapper) PushUpTemplateController
				.getResponseWrappers(request).get(slot);

		Assert.notNull(responseWrapper,
				"No wrapped response found for slot: " + slot);

		responseWrapper.renderResponse(response);
		return null;
	}

}
