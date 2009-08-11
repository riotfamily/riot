package org.riotfamily.common.hibernate;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class AbstractConditionalSetupBean extends AbstractSetupBean {

	private String condition;

	public AbstractConditionalSetupBean(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	protected final void setup(Session session) throws Exception {
		if (isSetupRequired(session)) {
			doSetup(session);
		}
	}
	
	protected abstract void doSetup(Session session);

	private boolean isSetupRequired(Session session) {
		if (condition == null) {
			return true;
		}
		Query query = session.createQuery(condition).setMaxResults(1);
		Object test = query.uniqueResult();
		if (test instanceof Number) {
			return ((Number) test).intValue() == 0;
		}
		if (test instanceof Boolean) {
			return ((Boolean) test).booleanValue();
		}
		return test == null;
	}
	
}
