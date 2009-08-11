package org.riotfamily.core.security.session;

import org.riotfamily.core.security.auth.RiotUserDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * BeanPostProcessor that wraps all {@link RiotUserDao} instances with a 
 * {@link RiotUserDaoWrapper}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RiotUserDaoProcessor implements BeanPostProcessor {

	public Object postProcessBeforeInitialization(Object bean, String beanName) 
			throws BeansException {
		
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String beanName) 
			throws BeansException {
		
		if (bean instanceof RiotUserDao) {
			return new RiotUserDaoWrapper((RiotUserDao) bean);
		}
		return bean;
	}

	
}
