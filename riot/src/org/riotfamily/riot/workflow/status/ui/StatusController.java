package org.riotfamily.riot.workflow.status.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.workflow.status.StatusMonitor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

public class StatusController implements Controller, 
		ApplicationContextAware {	
	
	private Collection monitors;
	
	private String viewName = ResourceUtils.getPath(
			StatusController.class, "StatusView.ftl");
	

	public void setApplicationContext(ApplicationContext context) {
		monitors = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				context, StatusMonitor.class).values();
	}
		
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (monitors.isEmpty()) {
			return null;
		}
		
		Locale locale = RequestContextUtils.getLocale(request);
		
		Iterator it = monitors.iterator();
		List model = new ArrayList();
		while (it.hasNext()) {
			StatusMonitor monitor = (StatusMonitor) it.next();
			Collection messages = monitor.getMessages(locale);
			if (messages != null) {
				model.addAll(messages);
			}
		}
		return new ModelAndView(viewName, "messages", model);
	}

}
