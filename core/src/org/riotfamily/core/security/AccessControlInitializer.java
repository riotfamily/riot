package org.riotfamily.core.security;

import java.util.ArrayList;
import java.util.Collections;

import org.riotfamily.core.security.policy.AuthorizationPolicy;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;

/**
 * Bean that injects static dependencies into the {@link AccessController}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class AccessControlInitializer implements ApplicationContextAware {
	
	@SuppressWarnings("unchecked")
	public void setApplicationContext(ApplicationContext context) {
		ArrayList policies = new ArrayList();
		policies.addAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(
				context, AuthorizationPolicy.class).values());
		
		Collections.sort(policies, new OrderComparator());
		AccessController.setPolicies(policies);
	}
	
}
