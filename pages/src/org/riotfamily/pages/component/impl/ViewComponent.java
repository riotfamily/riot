package org.riotfamily.pages.component.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.ViewResolverHelper;
import org.riotfamily.pages.component.ComponentVersion;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Component implementation that resolves a view-name just like Spring's
 * DispatcherServlet and renders the view passing the ComponentVersion's 
 * properties as model. 
 */
public class ViewComponent extends AbstractComponent 
		implements ApplicationContextAware {
	
	private ViewResolverHelper viewResolverHelper;

	private String viewName;
	
	private boolean dynamic = false;
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		viewResolverHelper = new ViewResolverHelper(applicationContext);
	}
	
	protected void renderInternal(ComponentVersion componentVersion, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Map model = buildModel(componentVersion);
		model.put(COMPONENT_ID, componentVersion.getId());
		model.put(POSITION_CLASS, positionClassName);
		ModelAndView mv = new ModelAndView(viewName, model);
		View view = viewResolverHelper.resolveView(request, mv);
		view.render(model, request, response);
	}
	
	public boolean isDynamic() {
		return this.dynamic;
	}
	
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
}
