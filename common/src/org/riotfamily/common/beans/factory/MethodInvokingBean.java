package org.riotfamily.common.beans.factory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.MethodInvoker;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MethodInvokingBean extends MethodInvoker 
		implements InitializingBean {
	
	public void setArgument(Object argument) {
		setArguments(new Object[] {argument});
	}
	
	public void afterPropertiesSet() throws Exception {
		prepare();
		invoke();
	}	

}
