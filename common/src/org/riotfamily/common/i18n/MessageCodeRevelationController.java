package org.riotfamily.common.i18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.RiotLog;
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

	private RiotLog log = RiotLog.get(
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
			messageSource.setContextPath(request.getContextPath());
		}
		return null;
	}
}
