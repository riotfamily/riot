package org.riotfamily.statistics.dao;

import org.hibernate.SessionFactory;

public abstract class AbstractHibernateStatisticsDao extends AbstractPropertiesDao {

	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
