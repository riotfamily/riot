package org.riotfamily.common.scheduling;

import org.springframework.core.Ordered;

/**
 * Abstract base class for scheduled tasks. Supports the setting of 
 * trigger names and the order via properties.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public abstract class ScheduledTaskSupport implements ScheduledTask, Ordered {

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

}
