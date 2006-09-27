package org.riotfamily.riot.security;

import java.util.ArrayList;
import java.util.Collections;

import org.riotfamily.riot.security.policy.AuthorizationPolicy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;

/**
 * Bean that can be used to configure the AccessController.
 */
public class AccessControlConfigurer implements ApplicationContextAware {

	public void setPrincipalBinder(PrincipalBinder principalBinder) {
		AccessController.setPrincipalBinder(principalBinder);
	}

	public void setApplicationContext(ApplicationContext context) {
		ArrayList policies = new ArrayList();
		policies.addAll(context.getBeansOfType(
				AuthorizationPolicy.class).values());
		
		Collections.sort(policies, new OrderComparator());
		AccessController.setPolicies(policies);	
	}

}
