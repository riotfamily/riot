package org.riotfamily.riot.hibernate.support;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateSupport {

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	protected Criteria createCriteria(Class clazz) {
		return getSession().createCriteria(clazz);
	}
	
	protected Query createQuery(String hql) {
		return getSession().createQuery(hql);
	}

}
