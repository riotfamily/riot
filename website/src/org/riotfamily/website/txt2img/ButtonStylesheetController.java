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
