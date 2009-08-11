package org.riotfamily.riot.hibernate.status;

import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.status.I18nStatusMonitor;
import org.springframework.beans.factory.annotation.Required;

public class HqlStatusMonitor extends I18nStatusMonitor {	
	
	private SessionFactory sessionFactory;
	
	private String hql;
	
	public HqlStatusMonitor(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Required
	public void setHql(String hql) {
		this.hql = hql;
	}

	protected Object[] getArgs(ScreenContext context) {	
		Object result = sessionFactory.getCurrentSession()
				.createQuery(hql)
				.uniqueResult();
		
		if (result == null) {
			return null;
		}
		else if (result instanceof Object[]) {
			return (Object[]) result;			
		}
		else {
			return new Object[] { result };
		}
	}

}
