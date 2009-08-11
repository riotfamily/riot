package org.riotfamily.common.scheduling;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.util.MethodInvoker;

public class MethodInvokingScheduledTask extends MethodInvoker 
		implements ScheduledTask, Ordered, InitializingBean {

	private String[] triggerNames;
	
	private int order = Ordered.LOWEST_PRECEDENCE;
	
	public String[] getTriggerNames() {
		return triggerNames;
	}
	
	public void setTriggerNames(String[] triggerNames) {
		this.triggerNames = triggerNames;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void afterPropertiesSet() throws Exception {
		prepare();
	}
	
	public void execute() throws Exception {
		invoke();
	}
}
