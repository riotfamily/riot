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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
