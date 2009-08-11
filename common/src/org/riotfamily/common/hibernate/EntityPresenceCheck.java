package org.riotfamily.common.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class EntityPresenceCheck implements FactoryBean {

	private SessionFactory sessionFactory;
	
	private Class<?> entityClass;
	
	private boolean invert;
	
	public EntityPresenceCheck(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	@Transactional
	public Object getObject() throws Exception {
		Assert.notNull(entityClass, "The entityClass must be specified");
		Number count = (Number) sessionFactory.getCurrentSession()
			.createCriteria(entityClass)
			.setProjection(Projections.rowCount())
			.uniqueResult();
	
		return invert ^ (count != null && count.intValue() > 0);
	}

	public Class<?> getObjectType() {
		return Boolean.class;
	}

	public boolean isSingleton() {
		return true;
	}

	
}
