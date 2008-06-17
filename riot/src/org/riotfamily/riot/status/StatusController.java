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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Controller that collects messages form all {@link StatusMonitor} beans
 * and displays them.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class StatusController implements Controller, 
		ApplicationContextAware {	
	
	private Collection<StatusMonitor> monitors;
	
	private String viewName = ResourceUtils.getPath(
			StatusController.class, "StatusView.ftl");
	

	@SuppressWarnings("unchecked")
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
		List<StatusMessage> model = new ArrayList<StatusMessage>();
		for (StatusMonitor monitor : monitors) {
			Collection<StatusMessage> messages = monitor.getMessages(locale);
			if (messages != null) {
				model.addAll(messages);
			}
		}
		return new ModelAndView(viewName, "messages", model);
	}

}
