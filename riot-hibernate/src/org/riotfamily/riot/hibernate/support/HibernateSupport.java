package org.riotfamily.riot.hibernate.support;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Base class for implementing DAOs based on plain Hibernate3 API.
 * Hibernate 3.0.1 introduced a feature called "contextual Sessions", 
 * where Hibernate itself manages one current Session per transaction. 
 * {@linkplain http://static.springframework.org/spring/docs/2.0.x/reference/orm.html#orm-hibernate-straight}
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
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
	
	protected Criteria createCriteria(Class<?> clazz) {
		return getSession().createCriteria(clazz);
	}
	
	protected Query createQuery(String hql) {
		return getSession().createQuery(hql);
	}
	
	protected Query createFilter(Object object, String hql) {
		return getSession().createFilter(object, hql);
	}

}
