package org.riotfamily.riot.workflow.notification.ui;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.common.web.view.JsonView;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.workflow.notification.NotificationDao;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class NotificationListController implements Controller {
	
	private static final String ID_PARAMETER = "id";
	
	private String viewName = ResourceUtils.getPath(
			NotificationListController.class, "NotificationView.ftl");
	
	private NotificationDao dao;	

	public NotificationListController(NotificationDao dao) {
		this.dao = dao;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (dao == null) {
			return null;
		}
		
		String userId = AccessController.getPrincipal(request);
		if (request.getParameter(ID_PARAMETER) != null) {
			Long notificationId = ServletRequestUtils.getLongParameter(
					request, ID_PARAMETER);
			
			dao.markAsRead(userId, notificationId);
			return null;
		}
		
		List notifications = dao.getNotifications(userId);
		
		if (ServletUtils.isXmlHttpRequest(request)) {
			return new ModelAndView(new JsonView(true), 
					"notifications", notifications);
		}
		else if (notifications != null && !notifications.isEmpty()) {
			return new ModelAndView(viewName);
		}
		return null;
		
	}
		
}
