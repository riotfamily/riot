package org.riotfamily.common.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SetupBean extends AbstractConditionalSetupBean {

	private List<?> objects;
	
	public SetupBean(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void setObjects(List<?> objects) {
		this.objects = objects;
	}

	@Override
	protected void doSetup(Session session) {
		for (Object object : objects) {
			session.save(object);
		}
	}
	
}
