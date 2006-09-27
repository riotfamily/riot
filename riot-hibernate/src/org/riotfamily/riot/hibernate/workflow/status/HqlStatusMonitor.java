package org.riotfamily.riot.hibernate.workflow.status;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.riot.workflow.status.support.AbstractStatusMonitor;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class HqlStatusMonitor extends AbstractStatusMonitor {	
	
	private SessionFactory sessionFactory;
	
	private String hql;
	
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setHql(String hql) {
		this.hql = hql;
	}

	protected Object[] getArgs() {		
		Object result = new HibernateTemplate(sessionFactory).execute(
				new HibernateCallback() {
					
			public Object doInHibernate(Session session) 
					throws HibernateException, SQLException {
				
				Query query = session.createQuery(hql);
				return query.uniqueResult();
			}		
		});
		
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
