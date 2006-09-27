package org.riotfamily.riot.hibernate.support;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

public class HibernatePropertyEditor extends PropertyEditorSupport {

	private SessionFactory sessionFactory;
	
	private Class entityClass;
	
	public HibernatePropertyEditor(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

	public String getAsText() {
		Object entity = getValue();
		if (entity == null) {
			return null;
		}
		return HibernateUtils.getIdAsString(sessionFactory, entity);
	}
	
	public void setAsText(String id) throws IllegalArgumentException {
		if (id == null) {
			setValue(null);
		}
		Assert.notNull(entityClass, "An entityClass must be set");
		
		Serializable sid = HibernateUtils.convertId(
				entityClass, id, sessionFactory);
				
		Object entity = new HibernateTemplate(sessionFactory).get(
				entityClass, sid);
	
		setValue(entity);
	}

}
