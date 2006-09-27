package org.riotfamily.common.web.view;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

public class ControllerView implements View {

	private Controller controller;
	
	private ViewResolver viewResolver;

	
	public ControllerView(Controller controller, ViewResolver viewResolver) {
		this.controller = controller;
		this.viewResolver = viewResolver;
	}

	public String getContentType() {
		return null;
	}

	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (model != null) {
			WebUtils.exposeRequestAttributes(request, model);
		}
		
		ModelAndView mv = controller.handleRequest(request, response);
		if (mv != null) {
			View view;
			if (mv.isReference()) {
				Locale locale = RequestContextUtils.getLocale(request);
				view = viewResolver.resolveViewName(mv.getViewName(), locale);
			}
			else {
				view = mv.getView();
			}
			view.render(mv.getModel(), request, response);
		}
	}

}
