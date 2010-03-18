/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.dbmsgsrc.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

public class EditMessageFormController implements Controller {
	
	private String formView;
	
	private String successView;

	public EditMessageFormController(String formView, String successView) {
		this.formView = formView;
		this.successView = successView;
	}

	@Transactional
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			return processForm(request);
		}
		return showForm(request);
	}	

	protected ModelAndView showForm(HttpServletRequest request) {
		MessageBundleEntry entry = getMessageBundleEntry(request);
		ModelAndView mav = new ModelAndView(formView);
		mav.addObject("code", entry.getCode());
		Locale locale = RequestContextUtils.getLocale(request);
		mav.addObject("message", entry.getText(locale));
		return mav;
	}
	
	protected ModelAndView processForm(HttpServletRequest request) {
		MessageBundleEntry entry = getMessageBundleEntry(request);
		Locale locale = RequestContextUtils.getLocale(request);
		String messageText = request.getParameter("message-text");
		if (StringUtils.hasText(messageText)) {
			entry.addTranslation(locale, messageText);				
		}
		return new ModelAndView(successView);
	}	

	private MessageBundleEntry getMessageBundleEntry(HttpServletRequest request) {
		Long id = Long.valueOf(HandlerUrlUtils.getPathVariable(request, "bundleId"));
		return MessageBundleEntry.load(id);
	}

}
