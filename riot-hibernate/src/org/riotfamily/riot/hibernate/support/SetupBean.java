package org.riotfamily.riot.hibernate.support;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SetupBean extends HibernateDaoSupport implements InitializingBean {

	private List objects;

	private String condition;
		
	public void setObjects(List objects) {
		this.objects = objects;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	protected void initDao() throws Exception {
		if (setupRequired()) {
			Iterator it = objects.iterator();
			while (it.hasNext()) {
				Object object = it.next();
				getHibernateTemplate().save(object);
			}
		}
	}
	
	protected boolean setupRequired() {
		if (condition == null) {
			return true;
		}
		Object test = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) 
					throws HibernateException, SQLException {
				
				Query query = session.createQuery(condition);
				query.setMaxResults(1);
				return query.uniqueResult();
			};
		});
		if (test instanceof Number) {
			return ((Number) test).intValue() == 0;
		}
		if (test instanceof Boolean) {
			return ((Boolean) test).booleanValue();
		}
		return test == null;
	}
	
}
