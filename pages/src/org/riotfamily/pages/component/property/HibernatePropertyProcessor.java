package org.riotfamily.pages.component.property;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class HibernatePropertyProcessor extends AbstractSinglePropertyProcessor {

	private SessionFactory sessionFactory;
	
	private Class entityClass;
	
	public HibernatePropertyProcessor() {
	}

	public HibernatePropertyProcessor(String property, Class entityClass,
			SessionFactory sessionFactory) {
		
		this.entityClass = entityClass;
		this.sessionFactory = sessionFactory;
		setProperty(property);
	}

	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected String convertToString(Object object) {
		if (object == null) {
			return null;
		}
		return HibernateUtils.getIdAsString(sessionFactory, object);
	}
	
	protected Object resolveString(String s) {
		if (s == null) {
			return null;
		}
		Serializable sid = HibernateUtils.convertId(entityClass, s, 
				sessionFactory);
				
		return new HibernateTemplate(sessionFactory).get(entityClass, sid);
	}
	
}
