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
package org.riotfamily.common.i18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that toggles code revelation of a CodeRevlealingMessageSource
 * for the current user.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class MessageCodeRevelationController implements Controller, 
		BeanFactoryAware {

	private Log log = LogFactory.getLog(
			MessageCodeRevelationController.class);
	
	private CodeRevealingMessageSource messageSource;
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		try {
			messageSource = (CodeRevealingMessageSource) beanFactory.getBean(
					"messageSource", CodeRevealingMessageSource.class);
		}
		catch (BeanNotOfRequiredTypeException e) {
			log.info("MessageSource is not a CodeRevealingMessageSource - " +
					"Message code revelation is not available.");
		}
		catch (NoSuchBeanDefinitionException e) {
			log.info("No MessageSource found - " +
					"Message code revelation is not available.");
		}
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (messageSource != null) {
			boolean reveal = !messageSource.isRevealCodes();
			messageSource.setRevealCodes(reveal);
		}
		return null;
	}
}
