package org.riotfamily.common.scheduling;

import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.HibernateCallbackWithoutResult;
import org.riotfamily.common.hibernate.ThreadBoundHibernateTemplate;
import org.springframework.core.Ordered;

/**
 * Scheduled task that executes code within a Hibernate session.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public abstract class HibernateTask extends HibernateCallbackWithoutResult 
		implements ScheduledTask, Ordered {

	private String[] triggerNames;
	
	private int order = Ordered.LOWEST_PRECEDENCE;
	
	private SessionFactory sessionFactory;
	
	private ThreadBoundHibernateTemplate hibernateTemplate;
	
	public HibernateTask(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateTemplate = new ThreadBoundHibernateTemplate(sessionFactory);
	}
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
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

	public void execute() throws Exception {
		hibernateTemplate.execute(this);
	}
		
}
