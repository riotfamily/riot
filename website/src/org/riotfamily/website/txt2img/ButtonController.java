package org.riotfamily.website.txt2img;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ButtonController implements Controller {

	private ButtonService buttonService;
	
	public ButtonController(ButtonService buttonService) {
		this.buttonService = buttonService;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String style = (String) request.getAttribute("style");
		String label = request.getParameter("label"); 
		buttonService.serveImage(style, label, request, response);
		return null;
	}

}
